package com.tmmtmm.sdk.messagecore;

public interface MessageContentType {

    int ContentType_Unknown = 0;
    int ContentType_Text = 1;
    int ContentType_Image = 2;
    int ContentType_Voice = 3;
    int ContentType_Video = 4;
    int ContentType_File = 5;
    int ContentType_Red_Packet = 6;
    int ContentType_Virtual_Currency_Transfer = 7;
    int ContentType_RTC = 8;
    int ContentType_Applet = 9;
    int ContentType_Moment = 10;
    int ContentType_Virtual_Currency_Pay= 11;
    int ContentType_Red_Packet_Center = 12;

    int ContentType_Location = 13;

    int ContentType_Meeting = 14;

    int ContentType_At = 15;

    int ContentType_Delete = 16;

    int ContentType_Uid_Text = 18;

    //Delete message, do not send this message directly, this message is a synchronous message when the server side is deleted
    int ContentType_Revoke = 81;

    //Read messages receipt
    int ContentType_Read_Receipt = 82;

}