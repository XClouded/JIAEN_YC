package com.cs.mms;
import android.content.Context;
import android.net.Uri;

import com.googles.android.mms.pdu.CharacterSets;
import com.googles.android.mms.pdu.EncodedStringValue;
import com.googles.android.mms.pdu.PduBody;
import com.googles.android.mms.pdu.PduComposer;
import com.googles.android.mms.pdu.PduPart;
import com.googles.android.mms.pdu.SendReq;

/**
* @author 
* @version ����ʱ�䣺2012-1-31 ����01:59:30
*/
public class MMSInfo {
        private Context con;
        private PduBody pduBody;
        private String recieverNum;
        private int partCount = 1;

        private static final String SUBJECT_STR = "4��XX���ѵĲ���"; // ��������

        public MMSInfo(Context con, String recieverNum) {
                // TODO Auto-generated constructor stub
                this.con = con;
                this.recieverNum = recieverNum;
                pduBody = new PduBody();
        }

        /**
         * ���ͼƬ������ÿ���һ������ñ�����һ�μ���
         * 
         * @author 
         * @param uriStr
         *            , �磺file://mnt/sdcard//1.jpg
         */
        public void addImagePart(String uriStr) {
                PduPart part = new PduPart();
                part.setCharset(CharacterSets.UTF_8);
                part.setName(("附件" + partCount++).getBytes());
//                part.setContentType(("image/jpg" + getTypeFromUri(uriStr)).getBytes());// "image/png"
                part.setContentType("image/jpg".getBytes());
                part.setDataUri(Uri.parse(uriStr));
                pduBody.addPart(part);
        }
        
//        public void addMusicPart(String uriStr) {
//            PduPart part = new PduPart();
//            part.setCharset(CharacterSets.UTF_8);
//            part.setName(("����" + partCount++).getBytes());
////            part.setContentType(("image/jpg" + getTypeFromUri(uriStr)).getBytes());// "image/png"
//            part.setContentType("image/jpg".getBytes());
//            part.setDataUri(Uri.parse(uriStr));
//            pduBody.addPart(part);
//    }

        /**
         * ͨ��URI·���õ�ͼƬ��ʽ���磺"file://mnt/sdcard//1.jpg" -----> "jpg"
         * 
         * @author 
         * @param uriStr
         * @return
         */
        private String getTypeFromUri(String uriStr) {
                return uriStr.substring(uriStr.lastIndexOf("."), uriStr.length());
        }

        /**
         * �����ŵ������Լ��������Ϣת����byte���飬׼��ͨ��httpЭ�鷢�͵�"http://mmsc.monternet.com"
         * 
         * @author ��
         * @return
         */
        public byte[] getMMSBytes() {
                PduComposer composer = new PduComposer(con, initSendReq());
                return composer.make();
        }

        /**
         * ��ʼ��SendReq
         * 
         * @author 
         * @return
         */
        private SendReq initSendReq() {
                SendReq req = new SendReq();
                EncodedStringValue[] sub = EncodedStringValue.extract(SUBJECT_STR);
                if (sub != null && sub.length > 0) {
                        req.setSubject(sub[0]);// ��������
                }
                EncodedStringValue[] rec = EncodedStringValue.extract(recieverNum);
                if (rec != null && rec.length > 0) {
                        req.addTo(rec[0]);// ���ý�����
                }
                req.setBody(pduBody);
                return req;
        }

}