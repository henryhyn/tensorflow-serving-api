package com.example;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.Test;
import org.tensorflow.example.Example;
import org.tensorflow.example.Feature;
import org.tensorflow.example.Features;
import org.tensorflow.example.FloatList;
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
public class ClientTest {
    @Test
    public void test1() {
        Model.ModelSpec.Builder modelSpec = Model.ModelSpec.newBuilder();
        modelSpec.setName("mnist");
        modelSpec.setSignatureName("serving_default");

        Predict.PredictRequest.Builder request = Predict.PredictRequest.newBuilder();
        request.setModelSpec(modelSpec);

        TensorShapeProto.Dim dim = TensorShapeProto.Dim.newBuilder().setSize(1).build();
        TensorShapeProto shape = TensorShapeProto.newBuilder().addDim(dim).build();

        TensorProto.Builder tensor = TensorProto.newBuilder();
        tensor.setTensorShape(shape);
        tensor.setDtype(DataType.DT_INT32);
        tensor.addIntVal(10);
        tensor.addIntVal(35);
        request.putInputs("input_label", tensor.build());

        tensor.clear();
        tensor.setTensorShape(shape);
        tensor.setDtype(DataType.DT_STRING);
        tensor.addStringVal(ByteString.copyFrom("新品,冰淇淋,不错,哈根达,斯,冰淇淋,不错,麻薯,口味,单一,口味,时代,吃,抹茶,口味,终于,选择,甜,吃,舒服,一点,吃,完,口,渴", StandardCharsets.UTF_8));
        tensor.addStringVal(ByteString.copyFrom("公园,太,太,指示,牌,指示,清晰,建议,地图,绿植,处,喷,淋,滋,水,旁边,座椅,头顶,树荫,处,那种,感觉,大胆,呼吸,天然,氧,赞", StandardCharsets.UTF_8));
        request.putInputs("input_feature", tensor.build());

        tensor.clear();
        tensor.setDtype(DataType.DT_FLOAT);
        tensor.addFloatVal(1.0F);
        request.putInputs("keep_prob", tensor.build());

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext(true).build();
        PredictionServiceGrpc.PredictionServiceBlockingStub stub = PredictionServiceGrpc.newBlockingStub(channel);
        Predict.PredictResponse response = stub.predict(request.build());
        System.out.println(response);
    }

    @Test
    public void test2() {
        FloatList.Builder floatList = FloatList.newBuilder();
        floatList.addValue(6.9F);
        floatList.addValue(3.1F);
        floatList.addValue(5.4F);
        floatList.addValue(2.1F);
        Feature feature = Feature.newBuilder().setFloatList(floatList).build();

        Features.Builder features = Features.newBuilder();
        features.putFeature("x", feature);

        Example example = Example.newBuilder().setFeatures(features).build();

        Model.ModelSpec.Builder modelSpec = Model.ModelSpec.newBuilder();
        modelSpec.setName("mnist");
        modelSpec.setSignatureName("serving_default");

        Predict.PredictRequest.Builder request = Predict.PredictRequest.newBuilder();
        request.setModelSpec(modelSpec);

        TensorShapeProto.Dim dim = TensorShapeProto.Dim.newBuilder().setSize(1).build();
        TensorShapeProto shape = TensorShapeProto.newBuilder().addDim(dim).build();

        TensorProto.Builder tensor = TensorProto.newBuilder();
        tensor.setTensorShape(shape);
        tensor.setDtype(DataType.DT_STRING);
        tensor.addStringVal(example.toByteString());
        request.putInputs("inputs", tensor.build());

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext(true).build();
        PredictionServiceGrpc.PredictionServiceBlockingStub stub = PredictionServiceGrpc.newBlockingStub(channel);
        Predict.PredictResponse response = stub.predict(request.build());
        System.out.println(response);
    }
}
