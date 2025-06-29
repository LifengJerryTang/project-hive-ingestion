package com.projecthive.summarization.utilities;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamoDbUtils {

    private DynamoDbUtils() {}

    public static Map<String, AttributeValue> convertAttributeMap(
            Map<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> lambdaAttributeMap) {
        Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> sdkAttributeMap = new HashMap<>();

        for (Map.Entry<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> entry : lambdaAttributeMap.entrySet()) {
            com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue lambdaAttr = entry.getValue();
            software.amazon.awssdk.services.dynamodb.model.AttributeValue.Builder sdkAttrBuilder =
                    software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder();

            if (lambdaAttr.getS() != null) {
                sdkAttrBuilder.s(lambdaAttr.getS());
            } else if (lambdaAttr.getN() != null) {
                sdkAttrBuilder.n(lambdaAttr.getN());
            } else if (lambdaAttr.getB() != null) {
                sdkAttrBuilder.b(SdkBytes.fromByteBuffer(lambdaAttr.getB()));
            } else if (lambdaAttr.getBOOL() != null) {
                sdkAttrBuilder.bool(lambdaAttr.getBOOL());
            } else if (lambdaAttr.getNULL() != null) {
                sdkAttrBuilder.nul(lambdaAttr.getNULL());
            } else if (lambdaAttr.getSS() != null) {
                sdkAttrBuilder.ss(lambdaAttr.getSS());
            } else if (lambdaAttr.getNS() != null) {
                sdkAttrBuilder.ns(lambdaAttr.getNS());
            } else if (lambdaAttr.getBS() != null) {
                sdkAttrBuilder.bs(lambdaAttr.getBS().stream()
                        .map(SdkBytes::fromByteBuffer)
                        .collect(Collectors.toList()));
            } else if (lambdaAttr.getM() != null) {
                sdkAttrBuilder.m(convertAttributeMap(lambdaAttr.getM()));
            } else if (lambdaAttr.getL() != null) {
                List<AttributeValue> convertedList =
                        new java.util.ArrayList<>();
                for (com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue item : lambdaAttr.getL()) {
                    Map<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> tempMap = new HashMap<>();
                    tempMap.put("temp", item);
                    convertedList.add(convertAttributeMap(tempMap).get("temp"));
                }
                sdkAttrBuilder.l(convertedList);
            }

            sdkAttributeMap.put(entry.getKey(), sdkAttrBuilder.build());
        }

        return sdkAttributeMap;
    }
}
