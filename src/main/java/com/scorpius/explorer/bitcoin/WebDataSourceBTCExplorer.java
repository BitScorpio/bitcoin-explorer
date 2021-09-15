package com.scorpius.explorer.bitcoin;

import com.scorpius.explorer.bitcoin.deserializer.BTCAddressDeserializer;
import com.scorpius.explorer.bitcoin.deserializer.BTCTransactionDeserializer;
import com.scorpius.explorer.bitcoin.transformer.BTCRecordResponseTransformer;
import dev.yasper.rump.config.RequestConfig;
import dev.yasper.rump.response.ResponseTransformer;

public abstract class WebDataSourceBTCExplorer implements BTCExplorer {

    protected final RequestConfig requestConfig;

    public WebDataSourceBTCExplorer(BTCAddressDeserializer addressDeserializer, BTCTransactionDeserializer transactionDeserializer) {
        ResponseTransformer responseTransformer = new BTCRecordResponseTransformer(addressDeserializer, transactionDeserializer);
        this.requestConfig = new RequestConfig().setResponseTransformer(responseTransformer);
    }
}
