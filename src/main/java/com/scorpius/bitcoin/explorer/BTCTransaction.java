package com.scorpius.bitcoin.explorer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BTCTransaction {

    public abstract String getHash();

    public abstract long getBlockHeight();

    public abstract long getFee();

    public abstract int getInputsCount();

    public abstract int getOutputsCount();

    public abstract BTCInput[] getInputs();

    public abstract BTCOutput[] getOutputs();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BTCTransaction)) {
            return false;
        }
        BTCTransaction transaction = (BTCTransaction) obj;
        return transaction.getHash().equals(getHash())
               && transaction.getBlockHeight() == getBlockHeight()
               && transaction.getFee() == getFee()
               && transaction.getInputsCount() == getInputsCount()
               && transaction.getOutputsCount() == getOutputsCount()
               && Arrays.equals(transaction.getInputs(), getInputs())
               && Arrays.equals(transaction.getOutputs(), getOutputs());
    }
}
