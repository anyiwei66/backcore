/**
 * Copyright {C} 2014 Coinport Inc. <http://www.coinport.com>
 *
 * WARNING:
 *  All structs must have at least 1 parameters, otherwise AKKA serialization fails.
 */

namespace java com.coinport.coinex.data

include "data.thrift"

///////////////////////////////////////////////////////////////////////
///////////////////////// PROCESSOR MESSAGES //////////////////////////

typedef data.ErrorCode              _ErrorCode
typedef data.Currency               _Currency
typedef data.Order                  _Order
typedef data.MarketDepth            _MarketDepth
typedef data.UserAccount            _UserAccount
typedef data.UserProfile            _UserProfile
typedef data.AccountTransfer        _AccountTransfer
typedef data.MarketSide             _MarketSide
typedef data.ApiSecret              _ApiSecret
typedef data.OrderInfo              _OrderInfo
typedef data.Metrics                _Metrics
typedef data.TransferStatus         _TransferStatus
typedef data.Cursor                 _Cursor
typedef data.SpanCursor             _SpanCursor
typedef data.EmailType              _EmailType
typedef data.Transaction            _Transaction
typedef data.TransactionItem        _TransactionItem
typedef data.CandleData             _CandleData
typedef data.ChartTimeDimension     _ChartTimeDimension
typedef data.QueryMarketSide        _QueryMarketSide
typedef data.ExportedEventType      _ExportedEventType
typedef data.HistoryAsset           _HistoryAsset
typedef data.CurrentAsset           _CurrentAsset
typedef data.HistoryPrice           _HistoryPrice
typedef data.CurrentPrice           _CurrentPrice
typedef data.TransferType           _TransferType
typedef data.BitwayType             _BitwayType
typedef data.GenerateWalletRequest  _GenerateWalletRequest
typedef data.TransferRequest        _TransferRequest
typedef data.QueryWalletRequest     _QueryWalletRequest
typedef data.GenerateWalletResponse _GenerateWalletResponse
typedef data.TransferResponse       _TransferResponse
typedef data.QueryWalletResponse    _QueryWalletResponse

///////////////////////////////////////////////////////////////////////
// 'C' stands for external command,
// 'P' stands for persistent event derived from a external command,
// 'Q' for query,
// 'I' stands for inter-processor commands
// 'R+' stands for response to sender on command success,
// 'R-' stands for response to sender on command failure,
// 'R' stands for response to sender regardless of failure or success.

// WARNING: please avoid using map in event definitation, if you do, please
// make sure all map keys are either i64, i32, double, float, or string;
// do not use enum, struct as map keys so our serialization can still work.

////////// General
/* R-   */ struct MessageNotSupported                 {1: string event}

////////// Admin
/* R    */ struct AdminCommandResult                  {1: _ErrorCode error = data.ErrorCode.OK}
/* C,P  */ struct TakeSnapshotNow                     {1: string desc, 2: optional i32 nextSnapshotinSeconds}
/* C    */ struct DumpStateToFile                     {1: string actorPath}

////////// UserProcessor
/* C,P  */ struct DoRegisterUser                      {1: _UserProfile userProfile, 2: string password}
/* R-   */ struct RegisterUserFailed                  {1: _ErrorCode error}
/* R+   */ struct RegisterUserSucceeded               {1: _UserProfile userProfile}

/* C,P  */ struct VerifyEmail                         {1: string token}
/* R-   */ struct VerifyEmailFailed                   {1: _ErrorCode error}
/* R+   */ struct VerifyEmailSucceeded                {1: i64 id, 2: string email}

/* C,P  */ struct DoUpdateUserProfile                 {1: _UserProfile userProfile}
/* R-   */ struct UpdateUserProfileFailed             {1: _ErrorCode error}
/* R+   */ struct UpdateUserProfileSucceeded          {1: _UserProfile userProfile /* previous profile */}

/* C,P  */ struct DoRequestPasswordReset              {1: string email, 2: optional string passwordResetToken /* ignored */}
/* R-   */ struct RequestPasswordResetFailed          {1: _ErrorCode error}
/* R+   */ struct RequestPasswordResetSucceeded       {1: i64 id, 2: string email}

/* Q    */ struct ValidatePasswordResetToken          {1: string passwordResetToken}
/* R    */ struct PasswordResetTokenValidationResult  {1: optional _UserProfile userProfile}

/* C,P  */ struct DoResetPassword                     {1: string newPassword, 2: string passwordResetToken}
/* R-   */ struct ResetPasswordFailed                 {1: _ErrorCode error}
/* R+   */ struct ResetPasswordSucceeded              {1: i64 id, 2: string email}

/* C    */ struct Login                               {1: string email, 2: string password} // TODO: this may also be a persistent command
/* R-   */ struct LoginFailed                         {1: _ErrorCode error}
/* R+   */ struct LoginSucceeded                      {1: i64 id, 2: string email}

/* Q    */ struct VerifyGoogleAuthCode                {1: string email, 2: i32 code}
/* R    */ struct GoogleAuthCodeVerificationResult    {1: optional _UserProfile userProfile}

/* C,P  */ struct DoRequestTransfer                   {1: _AccountTransfer transfer}
/* R-   */ struct RequestTransferFailed               {1: _ErrorCode error}
/* R+   */ struct RequestTransferSucceeded            {1: _AccountTransfer transfer}

/* C,P  */ struct AdminConfirmTransferFailure         {1: _AccountTransfer transfer, 2:_ErrorCode error}
/* C,P  */ struct AdminConfirmTransferSuccess         {1: _AccountTransfer transfer}

/* R-   */ struct AddRobotDNAFailed                   {1: _ErrorCode error, 2: i64 dnaId}
/* R+   */ struct AddRobotDNASucceeded                {1: i64 dnaId}

/* R-   */ struct RemoveRobotDNAFailed                {1: _ErrorCode error, 2: string robotIds}
/* R+   */ struct RemoveRobotDNASucceeded             {1: i64 dnaId}

/* C,P  */ struct DoSubmitOrder                       {1: _MarketSide side, 2: _Order order}
/* R-   */ struct SubmitOrderFailed                   {1: _MarketSide side, 2: _Order order, 3: _ErrorCode error}
/* I    */ struct OrderFundFrozen                     {1: _MarketSide side, 2: _Order order}

////////// ApiAuthProcessor
/* C,P  */ struct DoAddNewApiSecret                   {1: i64 userId}
/* C,P  */ struct DoDeleteApiSecret                   {1: _ApiSecret secret}
/* R    */ struct ApiSecretOperationResult            {1: _ErrorCode error, 2: list<_ApiSecret> secrets}

/* Q    */ struct QueryApiSecrets                     {1: i64 userId, 2: optional string identifier}
/* R    */ struct QueryApiSecretsResult               {1: i64 userId, 2: list<_ApiSecret> secrets}


////////// MarketProcessor
/* C,P  */ struct DoCancelOrder                       {1: _MarketSide side, 2: i64 id, 3: i64 userId}
/* R-   */ struct CancelOrderFailed                   {1: _ErrorCode error}

/* I,R+ */ struct OrderSubmitted                      {1: _OrderInfo originOrderInfo, 2: list<_Transaction> txs}
/* I,R+ */ struct OrderCancelled                      {1: _MarketSide side, 2: _Order order}

////////// RobotProcessor commands
/* C,P  */ struct DoUpdateMetrics                     {1: _Metrics metrics}
/* C,P  */ struct DoAddRobotDNA                       {1: map<string, string> states}
/* C,P  */ struct DoRemoveRobotDNA                    {1: i64 dnaId}

////////// Mailer
/* C    */ struct DoSendEmail                         {1: string email, 2: _EmailType emailType, 3: map<string, string> params}

////////// Bitway
/* C    */ struct BitwayRequest                       {1: _BitwayType type, 2: i64 requestId 3: _Currency currency, 4: optional _GenerateWalletRequest generateWalletRequest, 5: optional _TransferRequest transferRequest, 6: optional _QueryWalletRequest queryWalletRequest}
/* R    */ struct BitwayResponse                      {1: _BitwayType type, 2: i64 requestId 3: _Currency currency, 4: optional _GenerateWalletResponse generateWalletResponse, 5: optional _TransferResponse transferResponse, 6: optional _QueryWalletResponse queryWalletResponse}

////////////////////////////////////////////////////////////////
//////////////////////// VIEW MESSAGES /////////////////////////
////////////////////////////////////////////////////////////////

////////// AccountView
/* Q    */ struct QueryAccount                        {1: i64 userId}
/* R    */ struct QueryAccountResult                  {1: _UserAccount userAccount}

////////// MarketDepthView
/* Q    */ struct QueryMarketDepth                    {1: _MarketSide side, 2: i32 maxDepth}
/* R    */ struct QueryMarketDepthResult              {1: _MarketDepth marketDepth}

////////// CandleDataView
/* Q    */ struct QueryCandleData                     {1: _MarketSide side, 2: _ChartTimeDimension dimension, 3: i64 from, 4: i64 to}
/* R    */ struct QueryCandleDataResult               {1: _CandleData candleData}

////////// OrderView
/* Q    */ struct QueryOrder                          {1: optional i64 uid, 2: optional i64 oid, 3:optional i32 status, 4:optional _QueryMarketSide side, 5: _Cursor cursor, 6: bool getCount}
/* R    */ struct QueryOrderResult                    {1: list<_OrderInfo> orderinfos, 2: i64 count}

////////// TransactionView
/* Q    */ struct QueryTransaction                    {1: optional i64 tid, 2: optional i64 uid, 3: optional i64 oid, 4:optional _QueryMarketSide side, 5: _Cursor cursor, 6: bool getCount}
/* R    */ struct QueryTransactionResult              {1: list<_TransactionItem> transactionItems, 2: i64 count}

////////// which view?
/* Q    */ struct QueryTransfer                       {1: optional i64 uid, 2: optional _Currency currency, 3: optional _TransferStatus status, 4: optional _SpanCursor spanCur, 5:optional _TransferType type, 6: _Cursor cur, 7: bool getCount}
/* R    */ struct QueryTransferResult                 {1: list<_AccountTransfer> transfers, 2: i64 count}

////////// which view?
/* Q    */ struct QueryAsset                          {1: i64 uid, 2: i64 from, 3: i64 to}
/* R    */ struct QueryAssetResult                    {1: _CurrentAsset currentAsset, 2: _HistoryAsset historyAsset, 3: _CurrentPrice currentPrice, 4: _HistoryPrice historyPrice}


////////// EventExportToMongoView
/* Q    */ struct QueryExportToMongoState             {1: _ExportedEventType eventType}
/* R              TExportToMongoState */
