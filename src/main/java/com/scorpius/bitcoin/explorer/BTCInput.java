package com.scorpius.bitcoin.explorer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BTCInput {

    public abstract String getAddress();

    public abstract long getSatoshis();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BTCInput)) {
            return false;
        }
        BTCInput input = (BTCInput) obj;
        return input.getAddress().equals(getAddress())
               && input.getSatoshis() == getSatoshis();
    }
}
