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

typedef data.ErrorCode             ErrorCode
typedef data.Currency              Currency
typedef data.Order                 Order
typedef data.MarketDepth           MarketDepth
typedef data.UserAccount           UserAccount
typedef data.UserProfile           UserProfile
typedef data.Deposit               Deposit
typedef data.Withdrawal            Withdrawal
typedef data.MarketSide            MarketSide
typedef data.ApiSecret             ApiSecret
typedef data.OrderInfo             OrderInfo
typedef data.Metrics               Metrics
typedef data.TransferStatus        TransferStatus
typedef data.Cursor                Cursor
typedef data.SpanCursor            SpanCursor
typedef data.EmailType             EmailType
typedef data.Transaction           Transaction
typedef data.TransactionItem       TransactionItem
typedef data.CandleData            CandleData
typedef data.ChartTimeDimension    ChartTimeDimension
typedef data.QueryMarketSide       QueryMarketSide
typedef data.UserAsset             UserAsset
typedef data.MarketPrice           MarketPrice
typedef data.ExportedEventType     ExportedEventType
typedef data.DWItem                DWItem

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
/* R    */ struct AdminCommandResult                  {1: ErrorCode error = ErrorCode.OK}
/* C,P  */ struct TakeSnapshotNow                     {1: string desc, 2: optional i32 nextSnapshotinSeconds}
/* C    */ struct DumpStateToFile                     {1: string actorPath}

// TODO(d@): All token/password/code should be generated by frontend and use DoUpdateUserProfile to let bakend update profile.
////////// UserProcessor
/* C,P  */ struct DoRegisterUser                      {1: UserProfile userProfile, 2: string password}
/* R-   */ struct RegisterUserFailed                  {1: ErrorCode error}
/* R+   */ struct RegisterUserSucceeded               {1: UserProfile userProfile}

/* C,P  */ struct DoUpdateUserProfile                 {1: UserProfile userProfile}
/* R-   */ struct UpdateUserProfileFailed             {1: ErrorCode error}
/* R+   */ struct UpdateUserProfileSucceeded          {1: UserProfile userProfile /* previous profile */}

/* C,P  */ struct DoRequestPasswordReset              {1: string email}
/* R-   */ struct RequestPasswordResetFailed          {1: ErrorCode error}
/* R+   */ struct RequestPasswordResetSucceeded       {1: i64 id, 2: string email, 3: string passwordResetToken}

/* C,P  */ struct DoResetPassword                     {1: string email, 2: string password, 3: optional string passwordResetToken}
/* R-   */ struct ResetPasswordFailed                 {1: ErrorCode error}
/* R+   */ struct ResetPasswordSucceeded              {1: i64 id, 2: string email}

/* C    */ struct Login                               {1: string email, 2: string password} // TODO: this may also be a persistent command
/* R-   */ struct LoginFailed                         {1: ErrorCode error}
/* R+   */ struct LoginSucceeded                      {1: i64 id, 2: string email}

/* Q    */ struct ValidatePasswordResetToken          {1: string passwordResetToken}
/* R    */ struct PasswordResetTokenValidationResult  {1: optional UserProfile userProfile}

/* Q    */ struct VerifyGoogleAuthCode                {1: string email, 2: i32 code}
/* R    */ struct GoogleAuthCodeVerificationResult    {1: optional UserProfile userProfile}

/* C,P  */ struct DoRequestCashDeposit                {1: Deposit deposit}
/* R-   */ struct RequestCashDepositFailed            {1: ErrorCode error}
/* R+   */ struct RequestCashDepositSucceeded         {1: Deposit deposit}

/* C,P  */ struct DoRequestCashWithdrawal             {1: Withdrawal withdrawal}
/* R-   */ struct RequestCashWithdrawalFailed         {1: ErrorCode error}
/* R+   */ struct RequestCashWithdrawalSucceeded      {1: Withdrawal withdrawal}

/* C,P  */ struct AdminConfirmCashDepositFailure      {1: Deposit deposit, 2:ErrorCode error}
/* C,P  */ struct AdminConfirmCashDepositSuccess      {1: Deposit deposit}

/* C,P  */ struct AdminConfirmCashWithdrawalFailure   {1: Withdrawal withdrawal, 2: ErrorCode error}
/* C,P  */ struct AdminConfirmCashWithdrawalSuccess   {1: Withdrawal withdrawal}

/* C,P  */ struct DoSubmitOrder                       {1: MarketSide side, 2: Order order}
/* R-   */ struct SubmitOrderFailed                   {1: MarketSide side, 2: Order order, 3: ErrorCode error}
/* I    */ struct OrderFundFrozen                     {1: MarketSide side, 2: Order order}

////////// ApiAuthProcessor
/* C,P  */ struct DoAddNewApiSecret                   {1: i64 userId}
/* C,P  */ struct DoDeleteApiSecret                   {1: ApiSecret secret}
/* R    */ struct ApiSecretOperationResult            {1: ErrorCode error, 2: list<ApiSecret> secrets}

/* Q    */ struct QueryApiSecrets                     {1: i64 userId, 2: optional string identifier}
/* R    */ struct QueryApiSecretsResult               {1: i64 userId, 2: list<ApiSecret> secrets}


////////// MarketProcessor
/* C,P  */ struct DoCancelOrder                       {1: MarketSide side, 2: i64 id, 3: i64 userId}
/* R-   */ struct CancelOrderFailed                   {1: ErrorCode error}

/* I,R+ */ struct OrderSubmitted                      {1: OrderInfo originOrderInfo, 2: list<Transaction> txs}
/* I,R+ */ struct OrderCancelled                      {1: MarketSide side, 2: Order order}

////////// RobotProcessor commands
/* C,P  */ struct DoUpdateMetrics                     {1: Metrics metrics}

////////// Mailer
/* C    */ struct DoSendEmail                         {1: string email, 2: EmailType emailType, 3: map<string, string> params}

////////////////////////////////////////////////////////////////
//////////////////////// VIEW MESSAGES /////////////////////////
////////////////////////////////////////////////////////////////

////////// AccountView
/* Q    */ struct QueryAccount                        {1: i64 userId}
/* R    */ struct QueryAccountResult                  {1: UserAccount userAccount}

////////// MarketDepthView
/* Q    */ struct QueryMarketDepth                    {1: MarketSide side, 2: i32 maxDepth}
/* R    */ struct QueryMarketDepthResult              {1: MarketDepth marketDepth}

////////// CandleDataView
/* Q    */ struct QueryCandleData                     {1: MarketSide side, 2: ChartTimeDimension dimension, 3: i64 from, 4: i64 to}
/* R    */ struct QueryCandleDataResult               {1: CandleData candleData}

////////// OrderView
/* Q    */ struct QueryOrder                          {1: optional i64 uid, 2: optional i64 oid, 3:optional i32 status, 4:optional QueryMarketSide side, 5: Cursor cursor, 6: bool getCount}
/* R    */ struct QueryOrderResult                    {1: list<OrderInfo> orderinfos, 2: i64 count}

////////// TransactionView
/* Q    */ struct QueryTransaction                    {1: optional i64 tid, 2: optional i64 uid, 3: optional i64 oid, 4:optional QueryMarketSide side, 5: Cursor cursor, 6: bool getCount}
/* R    */ struct QueryTransactionResult              {1: list<TransactionItem> transactionItems, 2: i64 count}

////////// Deposit and WithDrawal Query
/* Q    */ struct QueryDW                             {1: optional i64 uid, 2: optional Currency currency, 3: optional TransferStatus status, 4: optional SpanCursor spanCur, 5:optional bool isDeposit, 6: Cursor cur, 7: bool getCount}
/* R    */ struct QueryDWResult                       {1: list<DWItem> dwitems, 2: i64 count}

////////// AssetQuery
/* Q    */ struct QueryAsset                          {1: i64 uid, 2: i64 from, 3: i64 to}
/* R    */ struct QueryAssetResult                    {1: map<i64, UserAsset> userAssets, 2: MarketPrice marketPrice}


////////// OpenData Query
/* Q    */ struct QueryExportToMongoState                {1: ExportedEventType eventType}
/* R              TExportToMongoState */