package com.kesierra.handler;

import com.alibaba.fastjson.JSONObject;
import com.kesierra.client.SmsServiceClient;
import com.kesierra.entity.FinishQue;
import com.kesierra.entity.SmsRequestBody;
import com.kesierra.entity.TrnParam;
import com.kesierra.enumP.ResponseCode;
import com.kesierra.util.RedisRaterLimiter;
import com.kesierra.util.RedissonLocker;
import com.kesierra.vo.Reponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class MessageLimitHandler {

    private static final String TELFLOWLOCK_KEY = "TELFLOWLOCK_KEY";
    private static final String FINISHLOCK_KEY = "FINISHLOCK_KEY";
    private static final String TELFLOWQ_KEY = "TELFLOWQ_KEY";
    private static final String GLOBALFLOWQ_KEY = "GLOBALFLOWQ_KEY";
    private static final String GLOBALFLOWLOCK_KEY = "GLOBALFLOWLOCK_KEY";
    private static final String FINISHQ_KEY = "FINISHQ_KEY";
    private static final String TELFLOWQ_TOKEN = "TELFLOWQ_TOKEN1";
    private static final String GLOBALFLOWQ_TOKEN = "GLOBALFLOWQ_TOKEN1";

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 10,
            60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));

    private static Timer myTimer = new Timer();
    @Autowired
    private RedissonLocker redissonLocker;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisRaterLimiter redisRaterLimiter;
    @Autowired
    private SmsServiceClient smsServiceClient;

//    public void addToTelFlowQue(TrnParam trnParam){
//        try{
//            redissonLocker.lock(TELFLOWLOCK_KEY, TimeUnit.SECONDS,4);
//
//            SmsRequestBody smsRequestBody = trnParam.getSmsRequestBody();
//            String tel = smsRequestBody.getAcceptor_tel();
//            String qos = smsRequestBody.getQos();
//            HashOperations opsHash = redisTemplate.opsForHash();
//            //获取手机流控队列，是否存在该手机号,如果不存在，直接添加进去
//            if (!opsHash.hasKey(TELFLOWQ_KEY,"tel")){
//                LinkedList<TrnParam> trnList = new LinkedList<>();
//                trnList.add(trnParam);
//                opsHash.put(TELFLOWQ_KEY,"tel",trnList);
//                return;
//            }
//            LinkedList<TrnParam> telFlowQue = (LinkedList<TrnParam>)opsHash.get(TELFLOWQ_KEY, "tel");
//            int size = telFlowQue.size();
//            if (size >= 4){
//                ListOperations opsList = redisTemplate.opsForList();
//                try {
//                    redissonLocker.lock(FINISHLOCK_KEY, TimeUnit.SECONDS, 4);
//                    //移除qos比较大的
//                    for (int i = 0; i < size; i++) {
//                        TrnParam trnParamTmp = telFlowQue.get(i);
//                        String tmpQos = trnParamTmp.getSmsRequestBody().getQos();
//                        if (tmpQos.compareToIgnoreCase(qos) > 0) {
//                            telFlowQue.remove(i);
//                            telFlowQue.add(trnParam);
//                            //添加移除数据到完成队列
//
//                            FinishQue finishQue = new FinishQue();
//                            finishQue.setCode(ResponseCode.Intenalrror.getCode());
//                            finishQue.setMessage(ResponseCode.Intenalrror.getMessage());
//                            finishQue.setReqID(trnParamTmp.getTrnNo());
//                            opsList.leftPush(FINISHQ_KEY, finishQue);
//                            return;
//
//                        }
//                    }
//                    //保存当前数据到完成队列里
//                    FinishQue finishQue = new FinishQue();
//                    finishQue.setCode(ResponseCode.Intenalrror.getCode());
//                    finishQue.setMessage(ResponseCode.Intenalrror.getMessage());
//                    finishQue.setReqID(trnParam.getTrnNo());
//                    opsList.leftPush(FINISHQ_KEY, finishQue);
//                    return;
//                }finally {
//                    redissonLocker.unlock(FINISHLOCK_KEY);
//                }
//            }else{
//                //添加到telflowq里
//                telFlowQue.add(trnParam);
//            }
//        }finally {
//            redissonLocker.unlock(TELFLOWLOCK_KEY);
//        }
//    }

    @PostConstruct //1在构造函数执行完之后执行
    public void init(){
        // 每一秒执行一次
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    handlerTask();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },  100,100);
    }

    /***
     *
     */
    @PreDestroy //2在bean销毁之前执行
    public void destroy(){
        myTimer.cancel();
        threadPool.shutdown();
    }
    public void addToGlobalQue(TrnParam trnParam){
        try{
            redissonLocker.lock(GLOBALFLOWLOCK_KEY, TimeUnit.SECONDS,4);
            ListOperations opsList = redisTemplate.opsForList();
            opsList.leftPush(GLOBALFLOWQ_KEY,trnParam);
        }finally {
            redissonLocker.unlock(GLOBALFLOWLOCK_KEY);
        }

    }
    public void addFinishQue(Reponse trnParam, String trnNo){
        try{
            redissonLocker.lock(FINISHLOCK_KEY, TimeUnit.SECONDS,4);
            HashOperations opsHash = redisTemplate.opsForHash();
            opsHash.put(FINISHQ_KEY,trnNo,trnParam);

        }finally {
            redissonLocker.unlock(FINISHLOCK_KEY);
        }
    }
    public Reponse getFinishQue(String trnNo){
        Reponse finishQue = new Reponse();
        finishQue.setCode(ResponseCode.Intenalrror.getCode());
        finishQue.setMessage(ResponseCode.Intenalrror.getMessage());
        try{
            //redissonLocker.lock(FINISHLOCK_KEY, TimeUnit.SECONDS,4);
            HashOperations opsHash = redisTemplate.opsForHash();
            Object object = opsHash.get(FINISHQ_KEY, trnNo);
            int times = 0 ;
            while (object == null && times <=30){
                Thread.sleep(100);
                object = opsHash.get(FINISHQ_KEY, trnNo);
                times++;
            }
            if (object != null){
                opsHash.delete(FINISHQ_KEY, trnNo);
                return (Reponse)object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           // redissonLocker.unlock(FINISHLOCK_KEY);
        }
        return finishQue;
    }
    public void handlerTask(){

        //System.out.println("i enter");
        try {
           // redissonLocker.lock(GLOBALFLOWLOCK_KEY, TimeUnit.SECONDS, 4);
            //HashOperations opsHash = redisTemplate.opsForHash();
            ListOperations opsList = redisTemplate.opsForList();
            TrnParam trnParam = (TrnParam) opsList.rightPop(GLOBALFLOWQ_KEY);
            while (trnParam != null){
                if (!redisRaterLimiter.tryAccess(TELFLOWQ_TOKEN+trnParam.getSmsRequestBody().getAcceptor_tel(), 1, 2)) {
                    opsList.leftPush(GLOBALFLOWQ_KEY, trnParam);
                    trnParam = (TrnParam)opsList.rightPop(GLOBALFLOWQ_KEY);
                }else{
                    if (!redisRaterLimiter.tryAccess(GLOBALFLOWQ_TOKEN, 10, 1) ){
                        return;
                    }

                    Reponse finishQue = new Reponse();
                    finishQue.setCode(ResponseCode.Intenalrror.getCode());
                    finishQue.setMessage(ResponseCode.Intenalrror.getMessage());
                    String trnNo = trnParam.getTrnNo();
                    //异步调用
                    TrnParam finalTrnParam = trnParam;
                    trnParam = (TrnParam)opsList.rightPop(GLOBALFLOWQ_KEY);
                    try{
                        threadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                //System.out.println("i enter"+new Date().toString());

                                //发送短信
                                try{
                                    JSONObject jsonObject = smsServiceClient.smsSend(finalTrnParam.getSmsRequestBody());
                                    System.out.println("i enter+"+jsonObject.toString());
                                    if ("0".equals(jsonObject.getString("res_code"))){
                                        finishQue.setCode(ResponseCode.sucess.getCode());
                                        finishQue.setMessage(ResponseCode.sucess.getMessage());
                                    }else{
                                        finishQue.setCode(ResponseCode.Intenalrror.getCode());
                                        finishQue.setMessage(ResponseCode.Intenalrror.getMessage());
                                    }


                                }catch(Exception e){
                                    e.printStackTrace();
                                    finishQue.setCode(ResponseCode.Intenalrror.getCode());
                                    finishQue.setMessage(ResponseCode.Intenalrror.getMessage());
                                }

                            }
                        });
                    }finally {
                        addFinishQue(finishQue, trnNo);
                    }



                }
            }
        }finally {
            //redissonLocker.unlock(GLOBALFLOWLOCK_KEY);
        }
    }

    public Reponse sendMessage (SmsRequestBody smsRequestBody){
        if (redisRaterLimiter.tryAccess(TELFLOWQ_TOKEN+smsRequestBody.getAcceptor_tel(), 1, 2)
        && redisRaterLimiter.tryAccess(GLOBALFLOWQ_TOKEN, 10, 1)) {
            Reponse reponse = new Reponse();
            try{
                System.out.println("i enter 2"+new Date().toString());
                JSONObject jsonObject = smsServiceClient.smsSend(smsRequestBody);
                System.out.println("i enter 2:"+jsonObject.toString());
                if ("0".equals(jsonObject.getString("res_code"))){
                    reponse.setCode(ResponseCode.sucess.getCode());
                    reponse.setMessage(ResponseCode.sucess.getMessage());
                }else{
                    reponse.setCode(ResponseCode.Intenalrror.getCode());
                    reponse.setMessage(ResponseCode.Intenalrror.getMessage());
                }


            }catch(Exception e){
                e.printStackTrace();
                reponse.setCode(ResponseCode.Intenalrror.getCode());
                reponse.setMessage(ResponseCode.Intenalrror.getMessage());
            }
            return reponse;
        }else{
            TrnParam trnParam = new TrnParam();
            trnParam.setSmsRequestBody(smsRequestBody);
            String trnNo = UUID.randomUUID().toString();
            trnParam.setTrnNo(trnNo);
            addToGlobalQue(trnParam);
            //获取交易状态
            return this.getFinishQue(trnNo);

        }
    }



}
