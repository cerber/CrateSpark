import java.sql.{Connection, DriverManager}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by cerber on 5/14/16.
  */
object CrateSparkDemo extends App {
  println("Crate.io test connection")
  val conf = new SparkConf().setAppName("TestSpectrum").setMaster("local")
  val sc = new SparkContext(conf)
  val rootLogger = Logger.getRootLogger.getAllAppenders.hasMoreElements
  if (!rootLogger) {
    Logger.getRootLogger.setLevel(Level.ERROR)
  }

  val sqlContext = new SQLContext(sc)

  println("Native Crate.io JDBC connection:")
  nativeCrateJDBC()

  println("Spark Crate.io JDBC connection:")
  sparkCrateJDBC()

  def sparkCrateJDBC(): Unit = {
    /*
      The Crate JDBC driver class is io.crate.client.jdbc.CrateDriver.
     */
    val jdbcDF = sqlContext.read.format("jdbc").options(
      Map(
        "url" -> "jdbc:crate://slave-01.cisco.com:4300",
        "driver" -> "io.crate.client.jdbc.CrateDriver",
        "dbtable" -> "demo_alltypes"
      )).load()
    jdbcDF.show()
  }

  def nativeCrateJDBC(): Unit = {
    // connect to the Crate.io database
    val driver = "io.crate.client.jdbc.CrateDriver"
    val url = "jdbc:crate://slave-01.cisco.com:4300"
    val username = "crate"
    val password = "crate"

    // there's probably a better way to do this
    var connection:Connection = null

    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("select * from demo where obj['gender'] = 'female'")
      while ( resultSet.next() ) {
        val name = resultSet.getString("name")
        val obj = resultSet.getString("obj")
        val tags = resultSet.getArray("tags")
        println("name, obj, tags = " + name + ", " + obj + ", " + tags)
      }
    } catch {
      case e: Throwable => e.printStackTrace()
    }
    connection.close()
  }

}
