package dev.wcirou;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

import java.util.Map;

public class Delete implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    //Creating Class Wide DynamoDB Client
    DynamoDbClient ddb = DynamoDbClient
            .builder()
            .region(Region.US_EAST_1)
            .build();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        //Getting http request type as a verification before delete is performed
        String httpMethod = input.getHttpMethod();
        if (httpMethod.equals("DELETE")) {
            //Getting User ID and deleting specified user from database
            String userId = input.getPathParameters().get("userId");

            DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                    .tableName("UserData")
                    .key(Map.of("UserId", AttributeValue.builder().s(userId).build()))
                    .build();

            ddb.deleteItem(deleteItemRequest);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("Delete Successful");
        }else{
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(405)
                    .withBody("Invalid HTTP Method, expecting delete");
        }
    }
}
