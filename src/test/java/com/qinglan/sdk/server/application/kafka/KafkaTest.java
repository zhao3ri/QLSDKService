package com.qinglan.sdk.server.application.kafka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;

import com.qinglan.sdk.server.release.BaseTestCase;

public class KafkaTest extends BaseTestCase {

    @Resource
    private KafkaProducerClient kafkaProducerClient;

    @Test
    public void kafkaTest() {
        for (int i=1;i<8;i++) {
            BufferedReader reader = null;
            File file = new File("/Users/engine/logs/release-sdk-manage/1/statis.log."+i+".2016-11-18");
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
//	            	System.out.print(tempString);
                    kafkaProducerClient.send(tempString);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {

                    }
                }
            }
        }

    }

}
