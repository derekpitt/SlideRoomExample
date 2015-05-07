package com.slideroom;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.SignatureException;

public class Main {

    public static void main(String[] args) {
	// write your code here

        try {
            RunNewExport();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (SlideRoomClientV2.Models.ApiErrorException e) {
            e.printStackTrace();
        }
    }

    public static void RunNewExport() throws SignatureException, IllegalArgumentException, IllegalAccessException, IOException, URISyntaxException, SlideRoomClientV2.Models.ApiErrorException {
        SlideRoomClientV2 client = new SlideRoomClientV2("<<api key here>>");

        SlideRoomClientV2.Models.RequestExportV2Parameters params = new SlideRoomClientV2.Models.RequestExportV2Parameters();
        params.format = SlideRoomClientV2.Models.RequestExportV2Parameters.FormatEnum.pdf;
        params.pdf = new SlideRoomClientV2.Models.RequestExportV2Parameters.PdfParameters();
        params.pdf.includecomments = true;
        params.searchname = "_Ted";

        SlideRoomClientV2.Models.RequestApplicationExportResult res = client.Application.RequestExportV2(params);
        System.out.println(res.token);

        // TODO: you can now check in on the token
    }

}
