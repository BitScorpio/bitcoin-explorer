package com.scorpius.blockchain.bitcoin.explorer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BTCOutput {

    public abstract String getAddress();

    public abstract long getSatoshis();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BTCOutput)) {
            return false;
        }
        BTCOutput input = (BTCOutput) obj;
        return input.getAddress().equals(getAddress())
               && input.getSatoshis() == getSatoshis();
    }
}
