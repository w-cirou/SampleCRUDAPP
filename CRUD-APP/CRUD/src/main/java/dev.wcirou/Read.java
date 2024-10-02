package dev.wcirou;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.util.Map;

public class Read implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    //Creating Class Wide DynamoDB Client
    DynamoDbClient ddb = DynamoDbClient
            .builder()
            .region(Region.US_EAST_1)
            .build();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        //Getting http request type as a verification before delete is performed
        String httpMethod = input.getHttpMethod();
        if (httpMethod.equals("GET")) {
            //Getting UserID and retrieving corresponding user data form database
            String userId = input.getQueryStringParameters().get("userId");

            GetItemRequest getItemrequest = GetItemRequest.builder()
                    .key(Map.of("UserId", AttributeValue.builder().s(userId).build()))
                    .tableName("UserData")
                    .build();

            Map<String,AttributeValue> item = ddb.getItem(getItemrequest).item();
            //Perform needed actions on item
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("Read Successful");
        }else{
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(405)
                    .withBody("Invalid HTTP Method, expecting get");
        }
    }
}
