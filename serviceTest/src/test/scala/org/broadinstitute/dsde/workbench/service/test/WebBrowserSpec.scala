package org.broadinstitute.dsde.workbench.service.test

import java.io.{File, FileInputStream, FileOutputStream}
import java.net.URL
import java.text.SimpleDateFormat
import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import org.broadinstitute.dsde.workbench.config.Config
import org.broadinstitute.dsde.workbench.service.Orchestration
import org.broadinstitute.dsde.workbench.service.util.ExceptionHandling
import org.openqa.selenium.chrome.{ChromeDriverService, ChromeOptions}
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.remote.{Augmenter, DesiredCapabilities, LocalFileDetector, RemoteWebDriver}
import org.openqa.selenium.{OutputType, TakesScreenshot, WebDriver}
import org.scalatest.Suite

import scala.collection.JavaConverters._
import scala.sys.SystemProperties
import scala.util.Random

/**
  * Base spec for writing FireCloud web browser tests.
  */
trait WebBrowserSpec extends WebBrowserUtil with ExceptionHandling with LazyLogging with RandomUtil { self: Suite =>

  val api = Orchestration

  /**
    * Executes a test in a fixture with a managed WebDriver. A test that uses
    * this will get its own WebDriver instance will be destroyed when the test
    * is complete. This encourages test case isolation.
    *
    * @param testCode the test code to run
    */
  def withWebDriver(testCode: (WebDriver) => Any): Unit = {
    withWebDriver(System.getProperty("java.io.tmpdir"))(testCode)
  }

  /**
    * Executes a test in a fixture with a managed WebDriver. A test that uses
    * this will get its own WebDriver instance will be destroyed when the test
    * is complete. This encourages test case isolation.
    *
    * @param downloadPath a directory where downloads should be saved
    * @param testCode the test code to run
    */
  def withWebDriver(downloadPath: String)(testCode: (WebDriver) => Any): Unit = {
    val headless = new SystemProperties().get("headless") match {
      case Some("false") => false
      case _ => true
    }
    val capabilities = getChromeIncognitoOption(downloadPath, headless)
    if (headless) {
      runHeadless(capabilities, testCode)
    } else {
      runLocalChrome(capabilities, testCode)
    }
  }

  private def getChromeIncognitoOption(downloadPath: String, headless: Boolean): DesiredCapabilities = {
    val fullDownloadPath = if (headless) s"/app/$downloadPath" else new File(downloadPath).getAbsolutePath
    logger.info(s"Chrome download path: $fullDownloadPath")
    val options = new ChromeOptions
    options.addArguments("--incognito")
    if (java.lang.Boolean.parseBoolean(System.getProperty("burp.proxy"))) {
      options.addArguments("--proxy-server=http://127.0.0.1:8080")
    }
    // Note that download.prompt_for_download will be ignored if download.default_directory is invalid or doesn't exist
    options.setExperimentalOption("prefs", Map(
      "download.default_directory" -> fullDownloadPath,
      "download.prompt_for_download" -> "false").asJava)
    val capabilities = DesiredCapabilities.chrome
    capabilities.setCapability(ChromeOptions.CAPABILITY, options)
    capabilities
  }

  private def runLocalChrome(capabilities: DesiredCapabilities, testCode: (WebDriver) => Any): Unit = {
    val service = new ChromeDriverService.Builder().usingDriverExecutable(new File(Config.ChromeSettings.chromeDriverPath)).usingAnyFreePort().build()
    service.start()
    implicit val driver: RemoteWebDriver = new RemoteWebDriver(service.getUrl, capabilities)
    driver.manage.window.setSize(new org.openqa.selenium.Dimension(1600, 2400))
    driver.setFileDetector(new LocalFileDetector())
    try {
      withScreenshot {
        testCode(driver)
      }
    } finally {
      try driver.quit() catch nonFatalAndLog
      try service.stop() catch nonFatalAndLog
    }
  }

  private def runHeadless(capabilities: DesiredCapabilities, testCode: (WebDriver) => Any): Unit = {
    val defaultChrome = Config.ChromeSettings.chromedriverHost
    implicit val driver: RemoteWebDriver = new RemoteWebDriver(new URL(defaultChrome), capabilities)
    driver.manage.window.setSize(new org.openqa.selenium.Dimension(1600, 2400))
    driver.setFileDetector(new LocalFileDetector())
    try {
      withScreenshot {
        testCode(driver)
      }
    } finally {
      try driver.quit() catch nonFatalAndLog
    }
  }

  /**
    * Override of withScreenshot that works with a remote Chrome driver and
    * lets us control the image file name.
    */
  override def withScreenshot[T](f: => T)(implicit driver: WebDriver): T = {
    try {
      f
    } catch {
      case t: Throwable =>
        val date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new java.util.Date())
        val fileName = s"failure_screenshots/${date}_$suiteName.png"
        val htmlSourceFileName = s"failure_screenshots/${date}_$suiteName.html"
        val logFileName = s"failure_screenshots/${date}_${suiteName}_console.txt"
        try {
          val directory = new File("failure_screenshots")
          if (!directory.exists()) {
            directory.mkdir()
          }
          val tmpFile = new Augmenter().augment(driver).asInstanceOf[TakesScreenshot].getScreenshotAs(OutputType.FILE)
          logger.error(s"Failure screenshot saved to $fileName")
          new FileOutputStream(new File(fileName)).getChannel.transferFrom(
            new FileInputStream(tmpFile).getChannel, 0, Long.MaxValue)

          val html = tagName("html").element.underlying.getAttribute("outerHTML")
          new FileOutputStream(new File(htmlSourceFileName)).write(html.getBytes)

          val logLines = driver.manage().logs().get(LogType.BROWSER).iterator().asScala.toList
          if (logLines.nonEmpty) {
            val logString = logLines.map(_.toString).reduce(_ + "\n" + _)
            new FileOutputStream(new File(logFileName)).write(logString.getBytes)
          }
        } catch nonFatalAndLog(s"FAILED TO SAVE SCREENSHOT $fileName")
        throw t
    }
  }
}
