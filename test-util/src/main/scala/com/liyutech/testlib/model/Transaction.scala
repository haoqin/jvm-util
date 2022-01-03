package com.liyutech.testlib.model

private[testlib] final case class Transaction(transactionId: Int, senderUsername: String, receiverUsername: String, expenseId: Int, message: String, amount: Double, isPending: Boolean)

