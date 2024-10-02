package dev.wcirou;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;

public class Update implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    //Creating Class Wide DynamoDB Client
    DynamoDbClient ddb = DynamoDbClient
            .builder()
            .region(Region.US_EAST_1)
            .build();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        //Getting http request type as a verification before delete is performed
        String httpMethod = input.getHttpMethod();

        //Getting User Data (ID and Password are Required, credit car information is optional)
        String userId = input.getPathParameters().get("userId");
        String userPassword = input.getPathParameters().get("userPassword");
        String userAddress = input.getPathParameters().get("userAddress");
        String userCreditCardNumber;
        String userCreditCardExpireDate;
        String userCreditCardCVV;
        String userCreditCardZipCode;

        //If no credit card information is in request then they are initialized as null
        try {
            userCreditCardNumber = input.getQueryStringParameters().get("userCreditCardNumber");
            userCreditCardExpireDate = input.getQueryStringParameters().get("userCreditCardExpireDate");
            userCreditCardCVV = input.getQueryStringParameters().get("userCreditCardCVV");
            userCreditCardZipCode = input.getQueryStringParameters().get("userCreditCardZipCode");
        } catch (Exception e) {
            System.out.println("No Credit Card Information Provided");
            userCreditCardNumber = null;
            userCreditCardExpireDate = null;
            userCreditCardCVV = null;
            userCreditCardZipCode = null;
        }

        //Updating either the userID, Password, and address only, or those items plus new credit card information
        if (httpMethod.equals("PATCH")) {
            PutItemRequest putItemRequest;


            if (userCreditCardNumber==null || userCreditCardNumber.isEmpty()){
                putItemRequest = PutItemRequest.builder()
                        .tableName("UserData")
                        .item(Map.of("UserID", AttributeValue.builder().s(userId).build(), "UserPassword",AttributeValue.builder().s(userPassword).build(),"UserAddress",AttributeValue.builder().s(userAddress).build()))
                        .build();
            }else{
                putItemRequest = PutItemRequest.builder()
                        .tableName("UserData")
                        .item(Map.of("UserID", AttributeValue.builder().s(userId).build(), "UserPassword", AttributeValue.builder().s(userPassword).build(), "UserAddress", AttributeValue.builder().s(userAddress).build(), "UserCreditCardNumber", AttributeValue.builder().s(userCreditCardNumber).build(), "UserCreditCardExpireDate", AttributeValue.builder().s(userCreditCardExpireDate).build(), "UserCreditCardCVV", AttributeValue.builder().s(userCreditCardCVV).build(), "UserCreditCardZipCode", AttributeValue.builder().s(userCreditCardZipCode).build()))
                        .build();
            }

            ddb.putItem(putItemRequest);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("Update Successful");
        }else{
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(405)
                    .withBody("Invalid HTTP Method, expecting patch");
        }
    }
}
