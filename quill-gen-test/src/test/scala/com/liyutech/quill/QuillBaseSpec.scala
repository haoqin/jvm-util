package com.liyutech.quill

import com.liyutech.common.ConfigUtil
import com.liyutech.quill.model.{Transaction, User}
import com.typesafe.config.Config
import io.getquill.{H2JdbcContext, SnakeCase}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.ScalacheckShapeless.derivedArbitrary
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckPropertyChecks}

import java.util.UUID

trait QuillBaseSpec extends AsyncFlatSpec with Checkers with ScalaCheckPropertyChecks {

  protected val userModelGen: Gen[User] = arbitrary[User].map(user => user.copy(UUID.randomUUID().toString.take(30)))
  protected def userModels(n: Int): Gen[Seq[User]] = Gen.listOfN(n, userModelGen)

  protected def autoIncrementModelGen: Gen[Transaction]= arbitrary[Transaction]
  protected def autoIncrementModels(n: Int): Gen[Seq[Transaction]] = Gen.listOfN(n, autoIncrementModelGen)

  protected val modelsPerIteration = 20

  protected val config: Config = ConfigUtil.loadConfig("test")
  protected val dbConfig: Config = config.getConfig("db")
  protected val quillDao = new H2JdbcContext(SnakeCase, dbConfig) with QuillGenericDao



}