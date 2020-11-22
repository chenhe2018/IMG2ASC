package app;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.avcodec;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class VideoConvert {

    public static void main(String[] args) throws Exception {

        // Flv2Imgs("http://101.132.110.90/group1/M00/00/05/rBN4LFq8p5SAJT0wA5k4vpHKf7Q325.mp4", "D:\\test", "test2");
        Flv2Imgs("C:\\Users\\ch\\Desktop\\马宝国-连五鞭.flv", "C:\\Users\\ch\\Desktop\\Thread", "test");
//        Flv2Imgs("C:\\Users\\ch\\Desktop\\马宝国-耗子尾汁.flv", "C:\\Users\\ch\\Desktop\\Thread", "test2");
        //Flv2Imgs("C:/Users\\Administrator\\Desktop\\VID_20171229_162251.mp4", "G:\\test", "111");
    }

    public static void FlvConvert() {

    }

    private static FFmpegFrameRecorder setRecorder(String rtmpUrl, FFmpegFrameGrabber fFmpegFrameGrabber) {
            // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(rtmpUrl, fFmpegFrameGrabber.getImageWidth(), fFmpegFrameGrabber.getImageHeight(), 1);
        recorder.setInterleaved(true);
        // 该参数用于降低延迟
        // recorder.setVideoOption("tune", "zerolatency");
        // ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；
        // 参考以下命令: ffmpeg -i '' -crf 30 -preset ultrafast
        recorder.setVideoOption("preset", "ultrafast");
        recorder.setVideoOption("crf", "30");
        // 视频编码器输出的比特率2000kbps/s
//        recorder.setVideoBitrate(2000000);
        recorder.setVideoBitrate(fFmpegFrameGrabber.getVideoBitrate());
        // H.264编码格式
//        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setVideoCodec(fFmpegFrameGrabber.getVideoCodec());
        // 提供输出流封装格式(rtmp协议只支持flv封装格式)
//        recorder.setFormat("flv");
        recorder.setFormat(fFmpegFrameGrabber.getFormat());
        // 视频帧率
//        recorder.setFrameRate(30);
        recorder.setFrameRate(fFmpegFrameGrabber.getFrameRate());
        // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍
        recorder.setGopSize(60);
        // 不可变(固定)音频比特率
        recorder.setAudioOption("crf", "0");
        // Highest quality
        recorder.setAudioQuality(0);
        // 音频比特率 192 Kbps
//        recorder.setAudioBitrate(192000);
        recorder.setAudioBitrate(fFmpegFrameGrabber.getAudioBitrate());
        // 频采样率
//        recorder.setSampleRate(44100);
        recorder.setSampleRate(fFmpegFrameGrabber.getSampleRate());
        // 双通道(立体声)
//        recorder.setAudioChannels(2);
        recorder.setAudioChannels(fFmpegFrameGrabber.getAudioChannels());
        // 音频编/解码器
//        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        recorder.setAudioCodec(fFmpegFrameGrabber.getAudioCodec());
        return recorder;


//        FFmpegFrameRecorder fFmpegFrameRecorder = null;
//        fFmpegFrameRecorder = new FFmpegFrameRecorder(targerFilePath + "\\" + targetFileName + ".flv", 1);
////        fFmpegFrameRecorder.setVideoCodecName(fFmpegFrameGrabber.getVideoCodec());//优先级高于videoCodec
//        fFmpegFrameRecorder.setVideoCodec(fFmpegFrameGrabber.getVideoCodec());//只有在videoCodecName没有设置或者没有找到videoCodecName的情况下才会使用videoCodec
//        fFmpegFrameRecorder.setFormat("flv");//只支持flv，mp4，3gp和avi四种格式，flv:AV_CODEC_ID_FLV1;mp4:AV_CODEC_ID_MPEG4;3gp:AV_CODEC_ID_H263;avi:AV_CODEC_ID_HUFFYUV;
//        fFmpegFrameRecorder.setPixelFormat(fFmpegFrameGrabber.getPixelFormat());// 只有pixelFormat，width，height三个参数任意一个不为空才会进行像素格式转换
////        fFmpegFrameRecorder.setGopSize(gopSize);//gop间隔
//        fFmpegFrameRecorder.setFrameRate(fFmpegFrameGrabber.getFrameRate());//帧率
//        fFmpegFrameRecorder.setVideoBitrate(fFmpegFrameGrabber.getVideoBitrate());
//        fFmpegFrameRecorder.setVideoQuality(0);
//        fFmpegFrameRecorder.setAudioOption("crf", "0");
//        fFmpegFrameRecorder.setAudioCodec(fFmpegFrameGrabber.getAudioCodec());
//        fFmpegFrameRecorder.setAudioBitrate(1);
//        fFmpegFrameRecorder.setAudioQuality(0);
//        fFmpegFrameRecorder.setAudioOption("aq", "10");
        //下面这三个参数任意一个会触发音频编码
//        fFmpegFrameRecorder.setSampleFormat(sampleRate);
//        fFmpegFrameRecorder.setAudioChannels(fFmpegFrameGrabber.getAudioChannels());
//        fFmpegFrameRecorder.setSampleRate(fFmpegFrameGrabber.getSampleRate());

    }

    /**
     * 视频按帧转换为多个图片
     *
     * @param filePath
     * @param targerFilePath
     * @param targetFileName
     * @throws Exception
     */
    public static void Flv2Imgs(String filePath, String targerFilePath, String targetFileName)
            throws Exception {
        Frame frame = null;
        IplImage iplImage = null;
        int count = 0;
        // 创建视频帧抓取工具
        FFmpegFrameGrabber fFmpegFrameGrabber = FFmpegFrameGrabber.createDefault(filePath);
        fFmpegFrameGrabber.start();
//        int ftp = fFmpegFrameGrabber.getLengthInFrames();//获取总帧数
        // 获取旋转角度信息（90度）
        String rotate = fFmpegFrameGrabber.getVideoMetadata("rotate");
        // 产生新的视频输出
        FFmpegFrameRecorder fFmpegFrameRecorder = null;
        fFmpegFrameRecorder = setRecorder(targerFilePath + "\\" + targetFileName + ".flv", fFmpegFrameGrabber);
        fFmpegFrameRecorder.start();
        //进入循环
        while ((frame = fFmpegFrameGrabber.grabFrame()) != null) {
            //一帧一帧去抓取视频图片，fFmpegFrameGrabber.grabImage();每次抓取下一帧
            //手机录的视频有旋转角度，需要旋转处理//导致没有声音，先注释
            if (null != rotate && rotate.length() > 1) {
                OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
                iplImage = converter.convert(frame);
                frame = converter.convert(rotate(iplImage, Integer.valueOf(rotate)));
            }
            //输出第几帧图片
            doExecuteFrame(frame, targerFilePath, targetFileName + ++count);
            try {
                String srcImgFile = targerFilePath + "\\" + targetFileName + count + ".jpg";
                String asciiImgFile = targerFilePath + "\\" + targetFileName + count + "-ascii.jpg";
                //进行字符画转换
                ImageConvert imageConvert = new ImageConvert();
                imageConvert.Image2Image(srcImgFile, asciiImgFile);
                BufferedImage bufferedImage = ImageIO.read(new File(asciiImgFile));
                Java2DFrameConverter java2dFrameConverter = new Java2DFrameConverter();
                Frame asciiFrame = java2dFrameConverter.convert(bufferedImage);
                frame.image = asciiFrame.image;
                frame.imageChannels =asciiFrame.imageChannels;
                frame.imageDepth =asciiFrame.imageDepth;
                frame.imageHeight =asciiFrame.imageHeight;
                frame.imageWidth =asciiFrame.imageWidth;
                frame.imageStride =asciiFrame.imageStride;

                fFmpegFrameRecorder.setTimestamp(fFmpegFrameGrabber.getTimestamp());
                fFmpegFrameRecorder.record(frame);
            } catch (Exception e) {
                // 会出现莫名的帧数处理错误，跳过
                System.out.println("跳过错误帧：" + count);
            }
        }

//        FrameGrabber grabberVideo = null;
//        Frame frameVideo;
//        //抓取音频帧
//        grabberVideo = new FFmpegFrameGrabber(filePath);
//        grabberVideo.start();
//        fFmpegFrameRecorder.setSampleRate(grabberVideo.getSampleRate());
//        //然后录入音频
//        while ((frameVideo = grabberVideo.grabFrame()) != null) {
//            fFmpegFrameRecorder.record(frameVideo);
//        }
//        grabberVideo.stop();
//        grabberVideo.release();


        fFmpegFrameGrabber.stop();
        fFmpegFrameGrabber.release();
        fFmpegFrameRecorder.stop();
        fFmpegFrameRecorder.release();
    }

    /**
     * 旋转角度
     *
     * @param src
     * @param angle
     * @return
     */
    public static IplImage rotate(IplImage src, int angle) {
        IplImage img = IplImage.create(src.height(), src.width(), src.depth(), src.nChannels());
        opencv_core.cvTranspose(src, img);
        opencv_core.cvFlip(img, img, angle);
        return img;
    }

    /**
     * 输出第几帧图片
     *
     * @param frame
     * @param targerFilePath
     * @param targetFileName
     */
    public static void doExecuteFrame(Frame frame, String targerFilePath, String targetFileName) {
        if (null == frame || null == frame.image) {
            return;
        }
        Java2DFrameConverter converter = new Java2DFrameConverter();
        String imageMat = "jpg";
        String FileName = targerFilePath + File.separator + targetFileName + "." + imageMat;
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        System.out.println(FileName + "\theight:" + bufferedImage.getHeight() + "\twidth:" + bufferedImage.getWidth());
        File output = new File(FileName);
        try {
            ImageIO.write(bufferedImage, imageMat, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}