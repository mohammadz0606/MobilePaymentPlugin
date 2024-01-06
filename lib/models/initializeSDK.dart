class InitializeSDK  {
  final String merchantId;
  final String secretKey;
  final String appleMerchantId;

  const InitializeSDK({
    this.merchantId = "AirrchipMerchant",
    this.secretKey = "MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh",
    this.appleMerchantId = "merchant.com.stspayone.demo",
  });

  toJson() {
   return {
     "merchantId": merchantId,
     "secretKey": secretKey,
     "appleMerchantId": appleMerchantId
   };
  }
}
