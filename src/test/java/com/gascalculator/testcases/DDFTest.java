package com.gascalculator.testcases;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gascalculator.pages.GasCalculatorPage;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DDFTest {
	
	WebDriver driver;
	
	Xls_Reader xl = new Xls_Reader("src/test/resources/testData.xlsx");
	
	
	@BeforeMethod
	public void setup() {
		
		WebDriverManager.chromedriver().setup();
		driver=new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.get("https://www.calculator.net/gas-mileage-calculator.html");
		
	}
	
	@AfterMethod
	public void closeup() {
		driver.quit();
	}
	
	
	@Test
	public void calculateTest() {
		
		GasCalculatorPage calcPage = new GasCalculatorPage(driver);
		
		
//		double currentOd =3454;
//		double previousOd = 2345;
//		double gas = 30;
		
		int rowcount=xl.getRowCount("data");
		
		for(int i = 2; i<=rowcount; i++) {
			
			String run = xl.getCellData("data", "Execute", i);
			if(!run.equalsIgnoreCase("Y")) {
				xl.setCellData("data", "Status", i, "Skip Requested");
				continue;
			}
			
			String currentOd = xl.getCellData("data", "CurrentOD", i);
			String previousOd = xl.getCellData("data", "PreviousOD", i);
			String gas = xl.getCellData("data", "Gas", i);
//			String expected = xl.getCellData("data", "Expected", i);
//			String actual = xl.getCellData("data", "Actual", i);
//			String status = xl.getCellData("data", "Status", i);
//			String time = xl.getCellData("datas", "Time", i);
			
			calcPage.currentOdometer.clear();
			calcPage.currentOdometer.sendKeys(currentOd);
			calcPage.previousOdometer.clear();
			calcPage.previousOdometer.sendKeys(previousOd);
			calcPage.gas.clear();
			calcPage.gas.sendKeys(gas);
			calcPage.calculate.click();
		
			String[] actualResult = calcPage.result.getText().split(" ");
			xl.setCellData("data", "Actual", i, actualResult[0]);
			System.out.println(actualResult[0]);
			
			
			double co = Double.parseDouble(currentOd);
			double po = Double.parseDouble(previousOd);
			double gs = Double.parseDouble(gas);
			
			
			double expectedResult = (co-po)/gs;
			DecimalFormat df = new DecimalFormat("0.00");
			String expectedResult2 = String.valueOf(df.format(expectedResult));
			xl.setCellData("data", "Expected", i, expectedResult2);
			
			if(actualResult[0].equals(expectedResult2)) {
				xl.setCellData("data", "Status", i, "Pass");
			}else {
				xl.setCellData("data", "Status", i, "Fail");
			}
			
			xl.setCellData("data", "Time", i, LocalDateTime.now().toString());
	}

		
}
}






















