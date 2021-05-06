package com.ppro.apiTests;

import com.ppro.utilities.ExcelUtil;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PproApiTests {


    @DataProvider
    public Object[][] userData() {
        ExcelUtil ppro = new ExcelUtil("src/test/resources/PPRO.xlsx", "Tabelle1");
        String[][] dataArray = ppro.getDataArrayWithoutFirstRow();
        return dataArray;
    }

    @Test(dataProvider = "userData")
    public void PostRequest(String amount, String currency, String country_code) {
        Integer amount2 = Integer.parseInt(amount);
        Map<String, Object> newPayload = new HashMap<>();
        newPayload.put("amount", amount2);
        newPayload.put("currency", currency);
        newPayload.put("country_code", country_code);

        Response response = given().log().all()
                .accept(ContentType.JSON)
                .and()
                .header("api_key", "secureKey")
                .and()
                .body(newPayload)
                .when()
                .post("https://automation-test-api.netlify.app/.netlify/functions/payment");

        assertEquals(response.statusCode(), 200);
        assertEquals(response.contentType(), "text/plain; charset=utf-8");
        assertTrue(response.body().asString().contains("Transaction succeeded"));

        JsonPath jsonPath=response.jsonPath();
        String orderIdStr=jsonPath.getString("data[0].orderId");
        Integer orderId=Integer.parseInt(orderIdStr);
        System.out.println("orderId=================> "+orderId);
        ExcelUtil.waitFor(1);
        //I have moved Get request here to benefit from iteration
        Response response2 = given()
                .header("api_key", "secureKey")
                .and()
                .queryParam("orderId", orderId)
                .when().get("https://automation-test-api.netlify.app/.netlify/functions/payment/");

        assertEquals(response2.statusCode(), 200);
        System.out.println("response2.statusCode()=========>"+response2.statusCode());
        assertEquals(response2.contentType(), "text/plain; charset=utf-8");
        assertTrue(response2.body().asString().contains("Pending"));
        ExcelUtil.waitFor(1);
    }


    @DataProvider
    public Object[][] userDataNegativeTesting() {
        ExcelUtil ppro = new ExcelUtil("src/test/resources/PPRO2.xlsx", "Tabelle1");
        String[][] dataArray = ppro.getDataArrayWithoutFirstRow();
        return dataArray;
    }

    @Test(dataProvider = "userDataNegativeTesting")
    public void PostRequestNegativeTesting(String amount, String currency, String country_code) {
        Integer amount2 = Integer.parseInt(amount);
        Map<String, Object> newPayload = new HashMap<>();
        newPayload.put("amount", amount2);
        newPayload.put("currency", currency);
        newPayload.put("country_code", country_code);

        Response response = given().log().all()
                .accept(ContentType.JSON)
                .and()
                .header("api_key", "secureKey")
                .and()
                .body(newPayload)
                .when()
                .post("https://automation-test-api.netlify.app/.netlify/functions/payment");

        assertEquals(response.statusCode(), 400);
        assertEquals(response.contentType(), "text/plain; charset=utf-8");
    }
}
