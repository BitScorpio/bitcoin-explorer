package com.scorpius.explorer.bitcoin.record;

import java.util.List;

public record BTCTransaction(String hash,
                             long blockHeight,
                             long fee,
                             int inputsCount,
                             int outputsCount,
                             List<BTCInput> inputs,
                             List<BTCOutput> outputs) {

}
