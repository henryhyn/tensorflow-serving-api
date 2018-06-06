package com.example;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import tensorflow.serving.Model;
import tensorflow.serving.Predict;
import tensorflow.serving.PredictionServiceGrpc;

import java.nio.charset.StandardCharsets;

/**
 * Created by Henry on 2018/6/6.
 */
public class Client {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext(true).build();
        PredictionServiceGrpc.PredictionServiceBlockingStub stub = PredictionServiceGrpc.newBlockingStub(channel);

        Model.ModelSpec.Builder modelSpec = Model.ModelSpec.newBuilder();
        modelSpec.setName("mnist");
        modelSpec.setSignatureName("serving_default");

        Predict.PredictRequest.Builder features = Predict.PredictRequest.newBuilder();
        features.setModelSpec(modelSpec);

        TensorShapeProto.Dim dim = TensorShapeProto.Dim.newBuilder().setSize(1).build();
        TensorShapeProto shape = TensorShapeProto.newBuilder().addDim(dim).build();

        TensorProto.Builder builder = TensorProto.newBuilder();
        builder.setTensorShape(shape);
        builder.setDtype(DataType.DT_INT32);
        builder.addIntVal(10);
        builder.addIntVal(35);
        features.putInputs("input_label", builder.build());

        builder.clear();
        builder.setTensorShape(shape);
        builder.setDtype(DataType.DT_STRING);
        builder.addStringVal(ByteString.copyFrom("新品,冰淇淋,不错,哈根达,斯,冰淇淋,不错,麻薯,口味,单一,口味,时代,吃,抹茶,口味,终于,选择,甜,吃,舒服,一点,吃,完,口,渴", StandardCharsets.UTF_8));
        builder.addStringVal(ByteString.copyFrom("公园,太,太,指示,牌,指示,清晰,建议,地图,绿植,处,喷,淋,滋,水,旁边,座椅,头顶,树荫,处,那种,感觉,大胆,呼吸,天然,氧,赞", StandardCharsets.UTF_8));
        features.putInputs("input_feature", builder.build());


        builder.clear();
        builder.setDtype(DataType.DT_FLOAT);
        builder.addFloatVal(1.0F);
        features.putInputs("keep_prob", builder.build());

        Predict.PredictResponse response = stub.predict(features.build());
        System.out.println(response);
    }
}
