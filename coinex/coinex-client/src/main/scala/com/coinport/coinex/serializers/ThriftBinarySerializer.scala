
/**
 * Copyright (C) 2014 Coinport Inc. <http://www.coinport.com>
 *
 * This file was auto generated by auto_gen_serializer.sh
 */

package com.coinport.coinex.serializers

import akka.serialization.Serializer
import com.twitter.bijection.scrooge.BinaryScalaCodec
import com.coinport.coinex.data._

class ThriftBinarySerializer extends Serializer {
  val includeManifest: Boolean = true
  val identifier = 607870725
  lazy val _cABCodeItem = BinaryScalaCodec(ABCodeItem)
  lazy val _cAccountTransfer = BinaryScalaCodec(AccountTransfer)
  lazy val _cApiSecret = BinaryScalaCodec(ApiSecret)
  lazy val _cBlockIndex = BinaryScalaCodec(BlockIndex)
  lazy val _cCandleData = BinaryScalaCodec(CandleData)
  lazy val _cCandleDataItem = BinaryScalaCodec(CandleDataItem)
  lazy val _cCashAccount = BinaryScalaCodec(CashAccount)
  lazy val _cCryptoCurrencyBlock = BinaryScalaCodec(CryptoCurrencyBlock)
  lazy val _cCryptoCurrencyTransaction = BinaryScalaCodec(CryptoCurrencyTransaction)
  lazy val _cCryptoCurrencyTransactionPort = BinaryScalaCodec(CryptoCurrencyTransactionPort)
  lazy val _cCryptoCurrencyTransferInfo = BinaryScalaCodec(CryptoCurrencyTransferInfo)
  lazy val _cCurrentAsset = BinaryScalaCodec(CurrentAsset)
  lazy val _cCurrentPrice = BinaryScalaCodec(CurrentPrice)
  lazy val _cCursor = BinaryScalaCodec(Cursor)
  lazy val _cExportOpenDataMap = BinaryScalaCodec(ExportOpenDataMap)
  lazy val _cFee = BinaryScalaCodec(Fee)
  lazy val _cHistoryAsset = BinaryScalaCodec(HistoryAsset)
  lazy val _cHistoryPrice = BinaryScalaCodec(HistoryPrice)
  lazy val _cMarketDepth = BinaryScalaCodec(MarketDepth)
  lazy val _cMarketDepthItem = BinaryScalaCodec(MarketDepthItem)
  lazy val _cMarketEvent = BinaryScalaCodec(MarketEvent)
  lazy val _cMarketSide = BinaryScalaCodec(MarketSide)
  lazy val _cMetrics = BinaryScalaCodec(Metrics)
  lazy val _cMetricsByMarket = BinaryScalaCodec(MetricsByMarket)
  lazy val _cOrder = BinaryScalaCodec(Order)
  lazy val _cOrderInfo = BinaryScalaCodec(OrderInfo)
  lazy val _cOrderUpdate = BinaryScalaCodec(OrderUpdate)
  lazy val _cQueryMarketSide = BinaryScalaCodec(QueryMarketSide)
  lazy val _cRedeliverFilterData = BinaryScalaCodec(RedeliverFilterData)
  lazy val _cRedeliverFilters = BinaryScalaCodec(RedeliverFilters)
  lazy val _cRefund = BinaryScalaCodec(Refund)
  lazy val _cSpanCursor = BinaryScalaCodec(SpanCursor)
  lazy val _cTMetricsObserver = BinaryScalaCodec(TMetricsObserver)
  lazy val _cTRobot = BinaryScalaCodec(TRobot)
  lazy val _cTStackQueue = BinaryScalaCodec(TStackQueue)
  lazy val _cTWindowQueue = BinaryScalaCodec(TWindowQueue)
  lazy val _cTWindowVector = BinaryScalaCodec(TWindowVector)
  lazy val _cTransaction = BinaryScalaCodec(Transaction)
  lazy val _cUserAccount = BinaryScalaCodec(UserAccount)
  lazy val _cUserLogsState = BinaryScalaCodec(UserLogsState)
  lazy val _cUserProfile = BinaryScalaCodec(UserProfile)
  lazy val _cAddRobotDNAFailed = BinaryScalaCodec(AddRobotDNAFailed)
  lazy val _cAddRobotDNASucceeded = BinaryScalaCodec(AddRobotDNASucceeded)
  lazy val _cAdminCommandResult = BinaryScalaCodec(AdminCommandResult)
  lazy val _cAdminConfirmTransferFailure = BinaryScalaCodec(AdminConfirmTransferFailure)
  lazy val _cAdminConfirmTransferSuccess = BinaryScalaCodec(AdminConfirmTransferSuccess)
  lazy val _cApiSecretOperationResult = BinaryScalaCodec(ApiSecretOperationResult)
  lazy val _cBitwayMessage = BinaryScalaCodec(BitwayMessage)
  lazy val _cBitwayRequest = BinaryScalaCodec(BitwayRequest)
  lazy val _cCancelOrderFailed = BinaryScalaCodec(CancelOrderFailed)
  lazy val _cCryptoCurrencyBlocksMessage = BinaryScalaCodec(CryptoCurrencyBlocksMessage)
  lazy val _cDoAddNewApiSecret = BinaryScalaCodec(DoAddNewApiSecret)
  lazy val _cDoAddRobotDNA = BinaryScalaCodec(DoAddRobotDNA)
  lazy val _cDoCancelOrder = BinaryScalaCodec(DoCancelOrder)
  lazy val _cDoDeleteApiSecret = BinaryScalaCodec(DoDeleteApiSecret)
  lazy val _cDoRegisterUser = BinaryScalaCodec(DoRegisterUser)
  lazy val _cDoRemoveRobotDNA = BinaryScalaCodec(DoRemoveRobotDNA)
  lazy val _cDoRequestACodeQuery = BinaryScalaCodec(DoRequestACodeQuery)
  lazy val _cDoRequestBCodeRecharge = BinaryScalaCodec(DoRequestBCodeRecharge)
  lazy val _cDoRequestConfirmRC = BinaryScalaCodec(DoRequestConfirmRC)
  lazy val _cDoRequestGenerateABCode = BinaryScalaCodec(DoRequestGenerateABCode)
  lazy val _cDoRequestPasswordReset = BinaryScalaCodec(DoRequestPasswordReset)
  lazy val _cDoRequestTransfer = BinaryScalaCodec(DoRequestTransfer)
  lazy val _cDoResetPassword = BinaryScalaCodec(DoResetPassword)
  lazy val _cDoSendEmail = BinaryScalaCodec(DoSendEmail)
  lazy val _cDoSubmitOrder = BinaryScalaCodec(DoSubmitOrder)
  lazy val _cDoUpdateMetrics = BinaryScalaCodec(DoUpdateMetrics)
  lazy val _cDoUpdateUserProfile = BinaryScalaCodec(DoUpdateUserProfile)
  lazy val _cDumpStateToFile = BinaryScalaCodec(DumpStateToFile)
  lazy val _cGenerateAddresses = BinaryScalaCodec(GenerateAddresses)
  lazy val _cGenerateAddressesResult = BinaryScalaCodec(GenerateAddressesResult)
  lazy val _cGetMissedCryptoCurrencyBlocks = BinaryScalaCodec(GetMissedCryptoCurrencyBlocks)
  lazy val _cGetNewAddress = BinaryScalaCodec(GetNewAddress)
  lazy val _cGetNewAddressResult = BinaryScalaCodec(GetNewAddressResult)
  lazy val _cGoogleAuthCodeVerificationResult = BinaryScalaCodec(GoogleAuthCodeVerificationResult)
  lazy val _cLogin = BinaryScalaCodec(Login)
  lazy val _cLoginFailed = BinaryScalaCodec(LoginFailed)
  lazy val _cLoginSucceeded = BinaryScalaCodec(LoginSucceeded)
  lazy val _cMessageNotSupported = BinaryScalaCodec(MessageNotSupported)
  lazy val _cMultiCryptoCurrencyTransactionMessage = BinaryScalaCodec(MultiCryptoCurrencyTransactionMessage)
  lazy val _cOrderCancelled = BinaryScalaCodec(OrderCancelled)
  lazy val _cOrderFundFrozen = BinaryScalaCodec(OrderFundFrozen)
  lazy val _cOrderSubmitted = BinaryScalaCodec(OrderSubmitted)
  lazy val _cPasswordResetTokenValidationResult = BinaryScalaCodec(PasswordResetTokenValidationResult)
  lazy val _cQueryAccount = BinaryScalaCodec(QueryAccount)
  lazy val _cQueryAccountResult = BinaryScalaCodec(QueryAccountResult)
  lazy val _cQueryApiSecrets = BinaryScalaCodec(QueryApiSecrets)
  lazy val _cQueryApiSecretsResult = BinaryScalaCodec(QueryApiSecretsResult)
  lazy val _cQueryAsset = BinaryScalaCodec(QueryAsset)
  lazy val _cQueryAssetResult = BinaryScalaCodec(QueryAssetResult)
  lazy val _cQueryCandleData = BinaryScalaCodec(QueryCandleData)
  lazy val _cQueryCandleDataResult = BinaryScalaCodec(QueryCandleDataResult)
  lazy val _cQueryMarketDepth = BinaryScalaCodec(QueryMarketDepth)
  lazy val _cQueryMarketDepthResult = BinaryScalaCodec(QueryMarketDepthResult)
  lazy val _cQueryOrder = BinaryScalaCodec(QueryOrder)
  lazy val _cQueryOrderResult = BinaryScalaCodec(QueryOrderResult)
  lazy val _cQueryRCDepositRecord = BinaryScalaCodec(QueryRCDepositRecord)
  lazy val _cQueryRCDepositRecordResult = BinaryScalaCodec(QueryRCDepositRecordResult)
  lazy val _cQueryRCWithdrawalRecord = BinaryScalaCodec(QueryRCWithdrawalRecord)
  lazy val _cQueryRCWithdrawalRecordResult = BinaryScalaCodec(QueryRCWithdrawalRecordResult)
  lazy val _cQueryTransaction = BinaryScalaCodec(QueryTransaction)
  lazy val _cQueryTransactionResult = BinaryScalaCodec(QueryTransactionResult)
  lazy val _cQueryTransfer = BinaryScalaCodec(QueryTransfer)
  lazy val _cQueryTransferResult = BinaryScalaCodec(QueryTransferResult)
  lazy val _cRegisterUserFailed = BinaryScalaCodec(RegisterUserFailed)
  lazy val _cRegisterUserSucceeded = BinaryScalaCodec(RegisterUserSucceeded)
  lazy val _cRemoveRobotDNAFailed = BinaryScalaCodec(RemoveRobotDNAFailed)
  lazy val _cRemoveRobotDNASucceeded = BinaryScalaCodec(RemoveRobotDNASucceeded)
  lazy val _cRequestACodeQueryFailed = BinaryScalaCodec(RequestACodeQueryFailed)
  lazy val _cRequestACodeQuerySucceeded = BinaryScalaCodec(RequestACodeQuerySucceeded)
  lazy val _cRequestBCodeRechargeFailed = BinaryScalaCodec(RequestBCodeRechargeFailed)
  lazy val _cRequestBCodeRechargeSucceeded = BinaryScalaCodec(RequestBCodeRechargeSucceeded)
  lazy val _cRequestConfirmRCFailed = BinaryScalaCodec(RequestConfirmRCFailed)
  lazy val _cRequestConfirmRCSucceeded = BinaryScalaCodec(RequestConfirmRCSucceeded)
  lazy val _cRequestGenerateABCodeFailed = BinaryScalaCodec(RequestGenerateABCodeFailed)
  lazy val _cRequestGenerateABCodeSucceeded = BinaryScalaCodec(RequestGenerateABCodeSucceeded)
  lazy val _cRequestPasswordResetFailed = BinaryScalaCodec(RequestPasswordResetFailed)
  lazy val _cRequestPasswordResetSucceeded = BinaryScalaCodec(RequestPasswordResetSucceeded)
  lazy val _cRequestTransferFailed = BinaryScalaCodec(RequestTransferFailed)
  lazy val _cRequestTransferSucceeded = BinaryScalaCodec(RequestTransferSucceeded)
  lazy val _cResetPasswordFailed = BinaryScalaCodec(ResetPasswordFailed)
  lazy val _cResetPasswordSucceeded = BinaryScalaCodec(ResetPasswordSucceeded)
  lazy val _cSubmitOrderFailed = BinaryScalaCodec(SubmitOrderFailed)
  lazy val _cTakeSnapshotNow = BinaryScalaCodec(TakeSnapshotNow)
  lazy val _cTransferCryptoCurrency = BinaryScalaCodec(TransferCryptoCurrency)
  lazy val _cUpdateUserProfileFailed = BinaryScalaCodec(UpdateUserProfileFailed)
  lazy val _cUpdateUserProfileSucceeded = BinaryScalaCodec(UpdateUserProfileSucceeded)
  lazy val _cValidatePasswordResetToken = BinaryScalaCodec(ValidatePasswordResetToken)
  lazy val _cVerifyEmail = BinaryScalaCodec(VerifyEmail)
  lazy val _cVerifyEmailFailed = BinaryScalaCodec(VerifyEmailFailed)
  lazy val _cVerifyEmailSucceeded = BinaryScalaCodec(VerifyEmailSucceeded)
  lazy val _cVerifyGoogleAuthCode = BinaryScalaCodec(VerifyGoogleAuthCode)
  lazy val _cTAccountState = BinaryScalaCodec(TAccountState)
  lazy val _cTAccountTransferState = BinaryScalaCodec(TAccountTransferState)
  lazy val _cTApiSecretState = BinaryScalaCodec(TApiSecretState)
  lazy val _cTAssetState = BinaryScalaCodec(TAssetState)
  lazy val _cTBitwayState = BinaryScalaCodec(TBitwayState)
  lazy val _cTCandleDataState = BinaryScalaCodec(TCandleDataState)
  lazy val _cTMarketState = BinaryScalaCodec(TMarketState)
  lazy val _cTMetricsState = BinaryScalaCodec(TMetricsState)
  lazy val _cTRobotState = BinaryScalaCodec(TRobotState)
  lazy val _cTSimpleState = BinaryScalaCodec(TSimpleState)
  lazy val _cTUserState = BinaryScalaCodec(TUserState)

  def toBinary(obj: AnyRef): Array[Byte] = obj match {
    case m: ABCodeItem => _cABCodeItem(m)
    case m: AccountTransfer => _cAccountTransfer(m)
    case m: ApiSecret => _cApiSecret(m)
    case m: BlockIndex => _cBlockIndex(m)
    case m: CandleData => _cCandleData(m)
    case m: CandleDataItem => _cCandleDataItem(m)
    case m: CashAccount => _cCashAccount(m)
    case m: CryptoCurrencyBlock => _cCryptoCurrencyBlock(m)
    case m: CryptoCurrencyTransaction => _cCryptoCurrencyTransaction(m)
    case m: CryptoCurrencyTransactionPort => _cCryptoCurrencyTransactionPort(m)
    case m: CryptoCurrencyTransferInfo => _cCryptoCurrencyTransferInfo(m)
    case m: CurrentAsset => _cCurrentAsset(m)
    case m: CurrentPrice => _cCurrentPrice(m)
    case m: Cursor => _cCursor(m)
    case m: ExportOpenDataMap => _cExportOpenDataMap(m)
    case m: Fee => _cFee(m)
    case m: HistoryAsset => _cHistoryAsset(m)
    case m: HistoryPrice => _cHistoryPrice(m)
    case m: MarketDepth => _cMarketDepth(m)
    case m: MarketDepthItem => _cMarketDepthItem(m)
    case m: MarketEvent => _cMarketEvent(m)
    case m: MarketSide => _cMarketSide(m)
    case m: Metrics => _cMetrics(m)
    case m: MetricsByMarket => _cMetricsByMarket(m)
    case m: Order => _cOrder(m)
    case m: OrderInfo => _cOrderInfo(m)
    case m: OrderUpdate => _cOrderUpdate(m)
    case m: QueryMarketSide => _cQueryMarketSide(m)
    case m: RedeliverFilterData => _cRedeliverFilterData(m)
    case m: RedeliverFilters => _cRedeliverFilters(m)
    case m: Refund => _cRefund(m)
    case m: SpanCursor => _cSpanCursor(m)
    case m: TMetricsObserver => _cTMetricsObserver(m)
    case m: TRobot => _cTRobot(m)
    case m: TStackQueue => _cTStackQueue(m)
    case m: TWindowQueue => _cTWindowQueue(m)
    case m: TWindowVector => _cTWindowVector(m)
    case m: Transaction => _cTransaction(m)
    case m: UserAccount => _cUserAccount(m)
    case m: UserLogsState => _cUserLogsState(m)
    case m: UserProfile => _cUserProfile(m)
    case m: AddRobotDNAFailed => _cAddRobotDNAFailed(m)
    case m: AddRobotDNASucceeded => _cAddRobotDNASucceeded(m)
    case m: AdminCommandResult => _cAdminCommandResult(m)
    case m: AdminConfirmTransferFailure => _cAdminConfirmTransferFailure(m)
    case m: AdminConfirmTransferSuccess => _cAdminConfirmTransferSuccess(m)
    case m: ApiSecretOperationResult => _cApiSecretOperationResult(m)
    case m: BitwayMessage => _cBitwayMessage(m)
    case m: BitwayRequest => _cBitwayRequest(m)
    case m: CancelOrderFailed => _cCancelOrderFailed(m)
    case m: CryptoCurrencyBlocksMessage => _cCryptoCurrencyBlocksMessage(m)
    case m: DoAddNewApiSecret => _cDoAddNewApiSecret(m)
    case m: DoAddRobotDNA => _cDoAddRobotDNA(m)
    case m: DoCancelOrder => _cDoCancelOrder(m)
    case m: DoDeleteApiSecret => _cDoDeleteApiSecret(m)
    case m: DoRegisterUser => _cDoRegisterUser(m)
    case m: DoRemoveRobotDNA => _cDoRemoveRobotDNA(m)
    case m: DoRequestACodeQuery => _cDoRequestACodeQuery(m)
    case m: DoRequestBCodeRecharge => _cDoRequestBCodeRecharge(m)
    case m: DoRequestConfirmRC => _cDoRequestConfirmRC(m)
    case m: DoRequestGenerateABCode => _cDoRequestGenerateABCode(m)
    case m: DoRequestPasswordReset => _cDoRequestPasswordReset(m)
    case m: DoRequestTransfer => _cDoRequestTransfer(m)
    case m: DoResetPassword => _cDoResetPassword(m)
    case m: DoSendEmail => _cDoSendEmail(m)
    case m: DoSubmitOrder => _cDoSubmitOrder(m)
    case m: DoUpdateMetrics => _cDoUpdateMetrics(m)
    case m: DoUpdateUserProfile => _cDoUpdateUserProfile(m)
    case m: DumpStateToFile => _cDumpStateToFile(m)
    case m: GenerateAddresses => _cGenerateAddresses(m)
    case m: GenerateAddressesResult => _cGenerateAddressesResult(m)
    case m: GetMissedCryptoCurrencyBlocks => _cGetMissedCryptoCurrencyBlocks(m)
    case m: GetNewAddress => _cGetNewAddress(m)
    case m: GetNewAddressResult => _cGetNewAddressResult(m)
    case m: GoogleAuthCodeVerificationResult => _cGoogleAuthCodeVerificationResult(m)
    case m: Login => _cLogin(m)
    case m: LoginFailed => _cLoginFailed(m)
    case m: LoginSucceeded => _cLoginSucceeded(m)
    case m: MessageNotSupported => _cMessageNotSupported(m)
    case m: MultiCryptoCurrencyTransactionMessage => _cMultiCryptoCurrencyTransactionMessage(m)
    case m: OrderCancelled => _cOrderCancelled(m)
    case m: OrderFundFrozen => _cOrderFundFrozen(m)
    case m: OrderSubmitted => _cOrderSubmitted(m)
    case m: PasswordResetTokenValidationResult => _cPasswordResetTokenValidationResult(m)
    case m: QueryAccount => _cQueryAccount(m)
    case m: QueryAccountResult => _cQueryAccountResult(m)
    case m: QueryApiSecrets => _cQueryApiSecrets(m)
    case m: QueryApiSecretsResult => _cQueryApiSecretsResult(m)
    case m: QueryAsset => _cQueryAsset(m)
    case m: QueryAssetResult => _cQueryAssetResult(m)
    case m: QueryCandleData => _cQueryCandleData(m)
    case m: QueryCandleDataResult => _cQueryCandleDataResult(m)
    case m: QueryMarketDepth => _cQueryMarketDepth(m)
    case m: QueryMarketDepthResult => _cQueryMarketDepthResult(m)
    case m: QueryOrder => _cQueryOrder(m)
    case m: QueryOrderResult => _cQueryOrderResult(m)
    case m: QueryRCDepositRecord => _cQueryRCDepositRecord(m)
    case m: QueryRCDepositRecordResult => _cQueryRCDepositRecordResult(m)
    case m: QueryRCWithdrawalRecord => _cQueryRCWithdrawalRecord(m)
    case m: QueryRCWithdrawalRecordResult => _cQueryRCWithdrawalRecordResult(m)
    case m: QueryTransaction => _cQueryTransaction(m)
    case m: QueryTransactionResult => _cQueryTransactionResult(m)
    case m: QueryTransfer => _cQueryTransfer(m)
    case m: QueryTransferResult => _cQueryTransferResult(m)
    case m: RegisterUserFailed => _cRegisterUserFailed(m)
    case m: RegisterUserSucceeded => _cRegisterUserSucceeded(m)
    case m: RemoveRobotDNAFailed => _cRemoveRobotDNAFailed(m)
    case m: RemoveRobotDNASucceeded => _cRemoveRobotDNASucceeded(m)
    case m: RequestACodeQueryFailed => _cRequestACodeQueryFailed(m)
    case m: RequestACodeQuerySucceeded => _cRequestACodeQuerySucceeded(m)
    case m: RequestBCodeRechargeFailed => _cRequestBCodeRechargeFailed(m)
    case m: RequestBCodeRechargeSucceeded => _cRequestBCodeRechargeSucceeded(m)
    case m: RequestConfirmRCFailed => _cRequestConfirmRCFailed(m)
    case m: RequestConfirmRCSucceeded => _cRequestConfirmRCSucceeded(m)
    case m: RequestGenerateABCodeFailed => _cRequestGenerateABCodeFailed(m)
    case m: RequestGenerateABCodeSucceeded => _cRequestGenerateABCodeSucceeded(m)
    case m: RequestPasswordResetFailed => _cRequestPasswordResetFailed(m)
    case m: RequestPasswordResetSucceeded => _cRequestPasswordResetSucceeded(m)
    case m: RequestTransferFailed => _cRequestTransferFailed(m)
    case m: RequestTransferSucceeded => _cRequestTransferSucceeded(m)
    case m: ResetPasswordFailed => _cResetPasswordFailed(m)
    case m: ResetPasswordSucceeded => _cResetPasswordSucceeded(m)
    case m: SubmitOrderFailed => _cSubmitOrderFailed(m)
    case m: TakeSnapshotNow => _cTakeSnapshotNow(m)
    case m: TransferCryptoCurrency => _cTransferCryptoCurrency(m)
    case m: UpdateUserProfileFailed => _cUpdateUserProfileFailed(m)
    case m: UpdateUserProfileSucceeded => _cUpdateUserProfileSucceeded(m)
    case m: ValidatePasswordResetToken => _cValidatePasswordResetToken(m)
    case m: VerifyEmail => _cVerifyEmail(m)
    case m: VerifyEmailFailed => _cVerifyEmailFailed(m)
    case m: VerifyEmailSucceeded => _cVerifyEmailSucceeded(m)
    case m: VerifyGoogleAuthCode => _cVerifyGoogleAuthCode(m)
    case m: TAccountState => _cTAccountState(m)
    case m: TAccountTransferState => _cTAccountTransferState(m)
    case m: TApiSecretState => _cTApiSecretState(m)
    case m: TAssetState => _cTAssetState(m)
    case m: TBitwayState => _cTBitwayState(m)
    case m: TCandleDataState => _cTCandleDataState(m)
    case m: TMarketState => _cTMarketState(m)
    case m: TMetricsState => _cTMetricsState(m)
    case m: TRobotState => _cTRobotState(m)
    case m: TSimpleState => _cTSimpleState(m)
    case m: TUserState => _cTUserState(m)

    case m => throw new IllegalArgumentException("Cannot serialize object: " + m)
  }

  def fromBinary(bytes: Array[Byte],
    clazz: Option[Class[_]]): AnyRef = clazz match {
    case Some(c) if c == classOf[ABCodeItem.Immutable] => _cABCodeItem.invert(bytes).get
    case Some(c) if c == classOf[AccountTransfer.Immutable] => _cAccountTransfer.invert(bytes).get
    case Some(c) if c == classOf[ApiSecret.Immutable] => _cApiSecret.invert(bytes).get
    case Some(c) if c == classOf[BlockIndex.Immutable] => _cBlockIndex.invert(bytes).get
    case Some(c) if c == classOf[CandleData.Immutable] => _cCandleData.invert(bytes).get
    case Some(c) if c == classOf[CandleDataItem.Immutable] => _cCandleDataItem.invert(bytes).get
    case Some(c) if c == classOf[CashAccount.Immutable] => _cCashAccount.invert(bytes).get
    case Some(c) if c == classOf[CryptoCurrencyBlock.Immutable] => _cCryptoCurrencyBlock.invert(bytes).get
    case Some(c) if c == classOf[CryptoCurrencyTransaction.Immutable] => _cCryptoCurrencyTransaction.invert(bytes).get
    case Some(c) if c == classOf[CryptoCurrencyTransactionPort.Immutable] => _cCryptoCurrencyTransactionPort.invert(bytes).get
    case Some(c) if c == classOf[CryptoCurrencyTransferInfo.Immutable] => _cCryptoCurrencyTransferInfo.invert(bytes).get
    case Some(c) if c == classOf[CurrentAsset.Immutable] => _cCurrentAsset.invert(bytes).get
    case Some(c) if c == classOf[CurrentPrice.Immutable] => _cCurrentPrice.invert(bytes).get
    case Some(c) if c == classOf[Cursor.Immutable] => _cCursor.invert(bytes).get
    case Some(c) if c == classOf[ExportOpenDataMap.Immutable] => _cExportOpenDataMap.invert(bytes).get
    case Some(c) if c == classOf[Fee.Immutable] => _cFee.invert(bytes).get
    case Some(c) if c == classOf[HistoryAsset.Immutable] => _cHistoryAsset.invert(bytes).get
    case Some(c) if c == classOf[HistoryPrice.Immutable] => _cHistoryPrice.invert(bytes).get
    case Some(c) if c == classOf[MarketDepth.Immutable] => _cMarketDepth.invert(bytes).get
    case Some(c) if c == classOf[MarketDepthItem.Immutable] => _cMarketDepthItem.invert(bytes).get
    case Some(c) if c == classOf[MarketEvent.Immutable] => _cMarketEvent.invert(bytes).get
    case Some(c) if c == classOf[MarketSide.Immutable] => _cMarketSide.invert(bytes).get
    case Some(c) if c == classOf[Metrics.Immutable] => _cMetrics.invert(bytes).get
    case Some(c) if c == classOf[MetricsByMarket.Immutable] => _cMetricsByMarket.invert(bytes).get
    case Some(c) if c == classOf[Order.Immutable] => _cOrder.invert(bytes).get
    case Some(c) if c == classOf[OrderInfo.Immutable] => _cOrderInfo.invert(bytes).get
    case Some(c) if c == classOf[OrderUpdate.Immutable] => _cOrderUpdate.invert(bytes).get
    case Some(c) if c == classOf[QueryMarketSide.Immutable] => _cQueryMarketSide.invert(bytes).get
    case Some(c) if c == classOf[RedeliverFilterData.Immutable] => _cRedeliverFilterData.invert(bytes).get
    case Some(c) if c == classOf[RedeliverFilters.Immutable] => _cRedeliverFilters.invert(bytes).get
    case Some(c) if c == classOf[Refund.Immutable] => _cRefund.invert(bytes).get
    case Some(c) if c == classOf[SpanCursor.Immutable] => _cSpanCursor.invert(bytes).get
    case Some(c) if c == classOf[TMetricsObserver.Immutable] => _cTMetricsObserver.invert(bytes).get
    case Some(c) if c == classOf[TRobot.Immutable] => _cTRobot.invert(bytes).get
    case Some(c) if c == classOf[TStackQueue.Immutable] => _cTStackQueue.invert(bytes).get
    case Some(c) if c == classOf[TWindowQueue.Immutable] => _cTWindowQueue.invert(bytes).get
    case Some(c) if c == classOf[TWindowVector.Immutable] => _cTWindowVector.invert(bytes).get
    case Some(c) if c == classOf[Transaction.Immutable] => _cTransaction.invert(bytes).get
    case Some(c) if c == classOf[UserAccount.Immutable] => _cUserAccount.invert(bytes).get
    case Some(c) if c == classOf[UserLogsState.Immutable] => _cUserLogsState.invert(bytes).get
    case Some(c) if c == classOf[UserProfile.Immutable] => _cUserProfile.invert(bytes).get
    case Some(c) if c == classOf[AddRobotDNAFailed.Immutable] => _cAddRobotDNAFailed.invert(bytes).get
    case Some(c) if c == classOf[AddRobotDNASucceeded.Immutable] => _cAddRobotDNASucceeded.invert(bytes).get
    case Some(c) if c == classOf[AdminCommandResult.Immutable] => _cAdminCommandResult.invert(bytes).get
    case Some(c) if c == classOf[AdminConfirmTransferFailure.Immutable] => _cAdminConfirmTransferFailure.invert(bytes).get
    case Some(c) if c == classOf[AdminConfirmTransferSuccess.Immutable] => _cAdminConfirmTransferSuccess.invert(bytes).get
    case Some(c) if c == classOf[ApiSecretOperationResult.Immutable] => _cApiSecretOperationResult.invert(bytes).get
    case Some(c) if c == classOf[BitwayMessage.Immutable] => _cBitwayMessage.invert(bytes).get
    case Some(c) if c == classOf[BitwayRequest.Immutable] => _cBitwayRequest.invert(bytes).get
    case Some(c) if c == classOf[CancelOrderFailed.Immutable] => _cCancelOrderFailed.invert(bytes).get
    case Some(c) if c == classOf[CryptoCurrencyBlocksMessage.Immutable] => _cCryptoCurrencyBlocksMessage.invert(bytes).get
    case Some(c) if c == classOf[DoAddNewApiSecret.Immutable] => _cDoAddNewApiSecret.invert(bytes).get
    case Some(c) if c == classOf[DoAddRobotDNA.Immutable] => _cDoAddRobotDNA.invert(bytes).get
    case Some(c) if c == classOf[DoCancelOrder.Immutable] => _cDoCancelOrder.invert(bytes).get
    case Some(c) if c == classOf[DoDeleteApiSecret.Immutable] => _cDoDeleteApiSecret.invert(bytes).get
    case Some(c) if c == classOf[DoRegisterUser.Immutable] => _cDoRegisterUser.invert(bytes).get
    case Some(c) if c == classOf[DoRemoveRobotDNA.Immutable] => _cDoRemoveRobotDNA.invert(bytes).get
    case Some(c) if c == classOf[DoRequestACodeQuery.Immutable] => _cDoRequestACodeQuery.invert(bytes).get
    case Some(c) if c == classOf[DoRequestBCodeRecharge.Immutable] => _cDoRequestBCodeRecharge.invert(bytes).get
    case Some(c) if c == classOf[DoRequestConfirmRC.Immutable] => _cDoRequestConfirmRC.invert(bytes).get
    case Some(c) if c == classOf[DoRequestGenerateABCode.Immutable] => _cDoRequestGenerateABCode.invert(bytes).get
    case Some(c) if c == classOf[DoRequestPasswordReset.Immutable] => _cDoRequestPasswordReset.invert(bytes).get
    case Some(c) if c == classOf[DoRequestTransfer.Immutable] => _cDoRequestTransfer.invert(bytes).get
    case Some(c) if c == classOf[DoResetPassword.Immutable] => _cDoResetPassword.invert(bytes).get
    case Some(c) if c == classOf[DoSendEmail.Immutable] => _cDoSendEmail.invert(bytes).get
    case Some(c) if c == classOf[DoSubmitOrder.Immutable] => _cDoSubmitOrder.invert(bytes).get
    case Some(c) if c == classOf[DoUpdateMetrics.Immutable] => _cDoUpdateMetrics.invert(bytes).get
    case Some(c) if c == classOf[DoUpdateUserProfile.Immutable] => _cDoUpdateUserProfile.invert(bytes).get
    case Some(c) if c == classOf[DumpStateToFile.Immutable] => _cDumpStateToFile.invert(bytes).get
    case Some(c) if c == classOf[GenerateAddresses.Immutable] => _cGenerateAddresses.invert(bytes).get
    case Some(c) if c == classOf[GenerateAddressesResult.Immutable] => _cGenerateAddressesResult.invert(bytes).get
    case Some(c) if c == classOf[GetMissedCryptoCurrencyBlocks.Immutable] => _cGetMissedCryptoCurrencyBlocks.invert(bytes).get
    case Some(c) if c == classOf[GetNewAddress.Immutable] => _cGetNewAddress.invert(bytes).get
    case Some(c) if c == classOf[GetNewAddressResult.Immutable] => _cGetNewAddressResult.invert(bytes).get
    case Some(c) if c == classOf[GoogleAuthCodeVerificationResult.Immutable] => _cGoogleAuthCodeVerificationResult.invert(bytes).get
    case Some(c) if c == classOf[Login.Immutable] => _cLogin.invert(bytes).get
    case Some(c) if c == classOf[LoginFailed.Immutable] => _cLoginFailed.invert(bytes).get
    case Some(c) if c == classOf[LoginSucceeded.Immutable] => _cLoginSucceeded.invert(bytes).get
    case Some(c) if c == classOf[MessageNotSupported.Immutable] => _cMessageNotSupported.invert(bytes).get
    case Some(c) if c == classOf[MultiCryptoCurrencyTransactionMessage.Immutable] => _cMultiCryptoCurrencyTransactionMessage.invert(bytes).get
    case Some(c) if c == classOf[OrderCancelled.Immutable] => _cOrderCancelled.invert(bytes).get
    case Some(c) if c == classOf[OrderFundFrozen.Immutable] => _cOrderFundFrozen.invert(bytes).get
    case Some(c) if c == classOf[OrderSubmitted.Immutable] => _cOrderSubmitted.invert(bytes).get
    case Some(c) if c == classOf[PasswordResetTokenValidationResult.Immutable] => _cPasswordResetTokenValidationResult.invert(bytes).get
    case Some(c) if c == classOf[QueryAccount.Immutable] => _cQueryAccount.invert(bytes).get
    case Some(c) if c == classOf[QueryAccountResult.Immutable] => _cQueryAccountResult.invert(bytes).get
    case Some(c) if c == classOf[QueryApiSecrets.Immutable] => _cQueryApiSecrets.invert(bytes).get
    case Some(c) if c == classOf[QueryApiSecretsResult.Immutable] => _cQueryApiSecretsResult.invert(bytes).get
    case Some(c) if c == classOf[QueryAsset.Immutable] => _cQueryAsset.invert(bytes).get
    case Some(c) if c == classOf[QueryAssetResult.Immutable] => _cQueryAssetResult.invert(bytes).get
    case Some(c) if c == classOf[QueryCandleData.Immutable] => _cQueryCandleData.invert(bytes).get
    case Some(c) if c == classOf[QueryCandleDataResult.Immutable] => _cQueryCandleDataResult.invert(bytes).get
    case Some(c) if c == classOf[QueryMarketDepth.Immutable] => _cQueryMarketDepth.invert(bytes).get
    case Some(c) if c == classOf[QueryMarketDepthResult.Immutable] => _cQueryMarketDepthResult.invert(bytes).get
    case Some(c) if c == classOf[QueryOrder.Immutable] => _cQueryOrder.invert(bytes).get
    case Some(c) if c == classOf[QueryOrderResult.Immutable] => _cQueryOrderResult.invert(bytes).get
    case Some(c) if c == classOf[QueryRCDepositRecord.Immutable] => _cQueryRCDepositRecord.invert(bytes).get
    case Some(c) if c == classOf[QueryRCDepositRecordResult.Immutable] => _cQueryRCDepositRecordResult.invert(bytes).get
    case Some(c) if c == classOf[QueryRCWithdrawalRecord.Immutable] => _cQueryRCWithdrawalRecord.invert(bytes).get
    case Some(c) if c == classOf[QueryRCWithdrawalRecordResult.Immutable] => _cQueryRCWithdrawalRecordResult.invert(bytes).get
    case Some(c) if c == classOf[QueryTransaction.Immutable] => _cQueryTransaction.invert(bytes).get
    case Some(c) if c == classOf[QueryTransactionResult.Immutable] => _cQueryTransactionResult.invert(bytes).get
    case Some(c) if c == classOf[QueryTransfer.Immutable] => _cQueryTransfer.invert(bytes).get
    case Some(c) if c == classOf[QueryTransferResult.Immutable] => _cQueryTransferResult.invert(bytes).get
    case Some(c) if c == classOf[RegisterUserFailed.Immutable] => _cRegisterUserFailed.invert(bytes).get
    case Some(c) if c == classOf[RegisterUserSucceeded.Immutable] => _cRegisterUserSucceeded.invert(bytes).get
    case Some(c) if c == classOf[RemoveRobotDNAFailed.Immutable] => _cRemoveRobotDNAFailed.invert(bytes).get
    case Some(c) if c == classOf[RemoveRobotDNASucceeded.Immutable] => _cRemoveRobotDNASucceeded.invert(bytes).get
    case Some(c) if c == classOf[RequestACodeQueryFailed.Immutable] => _cRequestACodeQueryFailed.invert(bytes).get
    case Some(c) if c == classOf[RequestACodeQuerySucceeded.Immutable] => _cRequestACodeQuerySucceeded.invert(bytes).get
    case Some(c) if c == classOf[RequestBCodeRechargeFailed.Immutable] => _cRequestBCodeRechargeFailed.invert(bytes).get
    case Some(c) if c == classOf[RequestBCodeRechargeSucceeded.Immutable] => _cRequestBCodeRechargeSucceeded.invert(bytes).get
    case Some(c) if c == classOf[RequestConfirmRCFailed.Immutable] => _cRequestConfirmRCFailed.invert(bytes).get
    case Some(c) if c == classOf[RequestConfirmRCSucceeded.Immutable] => _cRequestConfirmRCSucceeded.invert(bytes).get
    case Some(c) if c == classOf[RequestGenerateABCodeFailed.Immutable] => _cRequestGenerateABCodeFailed.invert(bytes).get
    case Some(c) if c == classOf[RequestGenerateABCodeSucceeded.Immutable] => _cRequestGenerateABCodeSucceeded.invert(bytes).get
    case Some(c) if c == classOf[RequestPasswordResetFailed.Immutable] => _cRequestPasswordResetFailed.invert(bytes).get
    case Some(c) if c == classOf[RequestPasswordResetSucceeded.Immutable] => _cRequestPasswordResetSucceeded.invert(bytes).get
    case Some(c) if c == classOf[RequestTransferFailed.Immutable] => _cRequestTransferFailed.invert(bytes).get
    case Some(c) if c == classOf[RequestTransferSucceeded.Immutable] => _cRequestTransferSucceeded.invert(bytes).get
    case Some(c) if c == classOf[ResetPasswordFailed.Immutable] => _cResetPasswordFailed.invert(bytes).get
    case Some(c) if c == classOf[ResetPasswordSucceeded.Immutable] => _cResetPasswordSucceeded.invert(bytes).get
    case Some(c) if c == classOf[SubmitOrderFailed.Immutable] => _cSubmitOrderFailed.invert(bytes).get
    case Some(c) if c == classOf[TakeSnapshotNow.Immutable] => _cTakeSnapshotNow.invert(bytes).get
    case Some(c) if c == classOf[TransferCryptoCurrency.Immutable] => _cTransferCryptoCurrency.invert(bytes).get
    case Some(c) if c == classOf[UpdateUserProfileFailed.Immutable] => _cUpdateUserProfileFailed.invert(bytes).get
    case Some(c) if c == classOf[UpdateUserProfileSucceeded.Immutable] => _cUpdateUserProfileSucceeded.invert(bytes).get
    case Some(c) if c == classOf[ValidatePasswordResetToken.Immutable] => _cValidatePasswordResetToken.invert(bytes).get
    case Some(c) if c == classOf[VerifyEmail.Immutable] => _cVerifyEmail.invert(bytes).get
    case Some(c) if c == classOf[VerifyEmailFailed.Immutable] => _cVerifyEmailFailed.invert(bytes).get
    case Some(c) if c == classOf[VerifyEmailSucceeded.Immutable] => _cVerifyEmailSucceeded.invert(bytes).get
    case Some(c) if c == classOf[VerifyGoogleAuthCode.Immutable] => _cVerifyGoogleAuthCode.invert(bytes).get
    case Some(c) if c == classOf[TAccountState.Immutable] => _cTAccountState.invert(bytes).get
    case Some(c) if c == classOf[TAccountTransferState.Immutable] => _cTAccountTransferState.invert(bytes).get
    case Some(c) if c == classOf[TApiSecretState.Immutable] => _cTApiSecretState.invert(bytes).get
    case Some(c) if c == classOf[TAssetState.Immutable] => _cTAssetState.invert(bytes).get
    case Some(c) if c == classOf[TBitwayState.Immutable] => _cTBitwayState.invert(bytes).get
    case Some(c) if c == classOf[TCandleDataState.Immutable] => _cTCandleDataState.invert(bytes).get
    case Some(c) if c == classOf[TMarketState.Immutable] => _cTMarketState.invert(bytes).get
    case Some(c) if c == classOf[TMetricsState.Immutable] => _cTMetricsState.invert(bytes).get
    case Some(c) if c == classOf[TRobotState.Immutable] => _cTRobotState.invert(bytes).get
    case Some(c) if c == classOf[TSimpleState.Immutable] => _cTSimpleState.invert(bytes).get
    case Some(c) if c == classOf[TUserState.Immutable] => _cTUserState.invert(bytes).get

    case Some(c) => throw new IllegalArgumentException("Cannot deserialize class: " + c.getCanonicalName)
    case None => throw new IllegalArgumentException("No class found in EventSerializer when deserializing array: " + bytes.mkString("").take(100))
  }
}
