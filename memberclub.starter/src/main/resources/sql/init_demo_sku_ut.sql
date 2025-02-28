use member_sku;
INSERT INTO member_sku.member_sku (id, biz_type, status, sale_info, finance_info, view_info, performance_info,
                                     restrict_info, inventory_info, extra, utime, ctime)
VALUES (200401, 1, 0, '{"originPriceFen":4500,"salePriceFen":4000}',
        '{"contractorId":"438098434","settlePriceFen":4000,"periodCycle":3,"financeProductType":1}',
        '{"displayImage":"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.alicdn.com%2Fbao%2Fuploaded%2Fi3%2F519685624%2FO1CN01Bb37dO1rPq9taBeml_%21%21519685624.jpg&refer=http%3A%2F%2Fimg.alicdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1742992970&t=a2e8db0911c3f759c109d95b5dc0082a","displayName":"京西Plus会员季卡","displayDesc":"有效期3个月;每月6张免运费券;Plus会员专属价","internalName":"电商会员季卡","internalDesc":"电商会员季卡"}',
        '{"configs":[{"bizType":1,"rightType":4,"rightId":32424,"assetCount":6,"periodCount":31,"periodType":1,"cycle":3,"providerId":"1","grantInfo":{},"settleInfo":{"contractorId":"438098434","settlePriceFen":30,"financeAssetType":4,"financeable":true},"viewInfo":{"displayName":"免运费券"},"saleInfo":{}},{"bizType":1,"rightType":5,"rightId":32423,"assetCount":0,"periodCount":31,"periodType":1,"cycle":3,"providerId":"2","grantInfo":{},"settleInfo":{"contractorId":"438098434","settlePriceFen":0,"financeAssetType":5,"financeable":false},"viewInfo":{"displayName":"会员价权益"},"saleInfo":{}},{"bizType":1,"rightType":3,"rightId":32425,"assetCount":0,"periodCount":31,"periodType":1,"cycle":3,"providerId":"3","grantInfo":{},"settleInfo":{"contractorId":"438098434","settlePriceFen":0,"financeAssetType":3,"financeable":false},"viewInfo":{"displayName":"会员身份"},"saleInfo":{}}]}',
        '{"enable":false}', '{"enable":false,"type":0}', '{}', 1740568165334, 1740568165334),
       (200402, 1, 0, '{"originPriceFen":1800,"salePriceFen":1500}',
        '{"contractorId":"438098434","settlePriceFen":1800,"periodCycle":1,"financeProductType":1}',
        '{"displayImage":"https://img0.baidu.com/it/u=973101599,73999428&fm=253&fmt=auto&app=138&f=JPEG?w=400&h=400","displayName":"京西Plus会员月卡","displayDesc":"有效期31天;6张免运费券;Plus会员专属价","internalName":"电商会员月卡","internalDesc":"电商会员月卡"}',
        '{"configs":[{"bizType":1,"rightType":4,"rightId":32424,"assetCount":6,"periodCount":31,"periodType":1,"cycle":1,"providerId":"1","grantInfo":{},"settleInfo":{"contractorId":"438098434","settlePriceFen":30,"financeAssetType":4,"financeable":true},"viewInfo":{"displayName":"免运费券"},"saleInfo":{}},{"bizType":1,"rightType":5,"rightId":32423,"assetCount":0,"periodCount":31,"periodType":1,"cycle":1,"providerId":"2","grantInfo":{},"settleInfo":{"contractorId":"438098434","settlePriceFen":0,"financeAssetType":5,"financeable":false},"viewInfo":{"displayName":"会员价权益"},"saleInfo":{}},{"bizType":1,"rightType":3,"rightId":32425,"assetCount":0,"periodCount":31,"periodType":1,"cycle":1,"providerId":"3","grantInfo":{},"settleInfo":{"contractorId":"438098434","settlePriceFen":0,"financeAssetType":3,"financeable":false},"viewInfo":{"displayName":"会员身份"},"saleInfo":{}}]}',
        '{"enable":false}', '{"enable":false,"type":0}', '{}', 1740568165895, 1740568165895),
       (200403, 2, 0, '{"originPriceFen":1000,"salePriceFen":600}',
        '{"contractorId":"438098434","settlePriceFen":600,"periodCycle":1,"financeProductType":1}',
        '{"displayImage":"https://img2.baidu.com/it/u=1205011088,2487597334&fm=253&fmt=auto&app=138&f=JPEG?w=530&h=500","displayName":"10元立减券","displayDesc":"无门槛立减券10元;有效期14天;过期退;有效期内限购4次","internalName":"10元立减券","internalDesc":"无门槛立减券10元"}',
        '{"configs":[{"bizType":2,"rightType":1,"rightId":32423,"assetCount":1,"periodCount":14,"periodType":1,"cycle":1,"providerId":"1","grantInfo":{},"settleInfo":{"contractorId":"438098434","settlePriceFen":1000,"financeAssetType":1,"financeable":true},"viewInfo":{"displayName":"10元立减券"},"saleInfo":{}}]}',
        '{"enable":true,"restrictItems":[{"periodType":"TOTAL","periodCount":14,"itemType":"TOTAL","userTypes":["USERID"],"total":4}]}',
        '{"enable":false,"type":0}', '{}', 1740568165898, 1740568165898),
       (200404, 2, 0, '{"originPriceFen":1500,"salePriceFen":900}',
        '{"contractorId":"438098434","settlePriceFen":900,"periodCycle":1,"financeProductType":1}',
        '{"displayImage":"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.alicdn.com%2Fbao%2Fuploaded%2Fi3%2F374544688%2FO1CN016Zx2lK1kV9QkrD6gW_%21%210-item_pic.jpg&refer=http%3A%2F%2Fimg.alicdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1742951021&t=9c87d26097559e952d220dff49a9d060","displayName":"15元混合券包","displayDesc":"无门槛组合券15元;有效期14天;过期退;有效期内限购4次","internalName":"15元混合券包","internalDesc":"无门槛组合券15元"}',
        '{"configs":[{"bizType":2,"rightType":1,"rightId":32424,"assetCount":1,"periodCount":14,"periodType":1,"cycle":1,"providerId":"1","grantInfo":{},"settleInfo":{"contractorId":"438098434","settlePriceFen":500,"financeAssetType":1,"financeable":true},"viewInfo":{"displayName":"5元立减券"},"saleInfo":{}},{"bizType":2,"rightType":1,"rightId":32423,"assetCount":1,"periodCount":14,"periodType":1,"cycle":1,"providerId":"1","grantInfo":{},"settleInfo":{"contractorId":"438098434","settlePriceFen":1000,"financeAssetType":1,"financeable":true},"viewInfo":{"displayName":"10元立减券"},"saleInfo":{}}]}',
        '{"enable":true,"restrictItems":[{"periodType":"TOTAL","periodCount":14,"itemType":"TOTAL","userTypes":["USERID"],"total":4}]}',
        '{"enable":false,"type":0}', '{}', 1740568165901, 1740568165901);
