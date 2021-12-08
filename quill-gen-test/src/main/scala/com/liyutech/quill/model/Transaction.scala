package com.liyutech.quill.model

private[quill] final case class Transaction(transactionId: Int, senderUsername: String, receiverUsername: String, expenseId: Int, message: String, amount: Double, isPending: Boolean)

