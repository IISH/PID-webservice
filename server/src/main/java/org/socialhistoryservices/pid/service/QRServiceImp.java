/*
 * The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
 *
 * Copyright (C) 2010-2012, International Institute of Social History
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.socialhistoryservices.pid.service;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import net.handle.hdllib.HandleException;
import org.socialhistoryservices.pid.database.dao.HandleDao;
import org.socialhistoryservices.pid.database.dao.HandleDaoImpl;
import org.socialhistoryservices.pid.database.domain.Handle;
import org.socialhistoryservices.pid.schema.PidType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;

/**
 * QRServiceImp
 * <p/>
 * Encodes PID values into a QR matrix image
 * Decodes a matrix imaged into a URL and resolves.
 */
public class QRServiceImp implements QRService {

    private HandleDao handleDao;
    private MappingsService mappingsService;
    private int width;
    private int height;

    @Override
    public PidType getPid(String pid) throws HandleException {
        return mappingsService.convertHandleToPidType(handleDao.fetchHandleByPID(pid));
    }

    /**
     * encode
     * <p/>
     * Retrieve the URL that is bound to the PID value.
     * If there are qualifiers, then we try to lookup the existence of it.
     *
     * @param pid handle
     * @return true if the resolve URL is found. Otherwise false
     * @throws Exception
     */
    @Override
    public byte[] encode(String handleResolverBaseUrl, String pid, String locatt, int width, int height) throws Exception {

        String[] split = (locatt == null) ? new String[]{""} : locatt.split(":", 2);
        String pair = (split.length == 2) ? split[0] + "=\"" + split[1] + "\"" : null;

        final List<Handle> handles = handleDao.fetchHandleByPID(pid);
        for (Handle handle : handles) {
            if (pair == null) return matriximage(handleResolverBaseUrl + pid, width, height);
            if (handle.getTypeAsString().equalsIgnoreCase(HandleDaoImpl.LOC)) {
                if (handle.getDataAsString().contains(pair)) {
                    return matriximage(handleResolverBaseUrl + pid + "?locatt=" + locatt, width, height);
                }
            }
        }

        return null;
    }

    public byte[] qr404image() throws IOException {
        final InputStream is = this.getClass().getResourceAsStream("/qr404.png");
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) {
            baos.write(b);
        }
        is.close();
        return baos.toByteArray();
    }

    private byte[] matriximage(String url, int width, int height) throws Exception {

        if (width < 1 || width > this.width) width = this.width;
        if (height < 1 || height > this.height) height = this.height;

        Charset charset = Charset.forName("ISO-8859-1");
        CharsetEncoder encoder = charset.newEncoder();
        // Convert a string to ISO-8859-1 bytes in a ByteBuffer
        ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(url));
        byte[] b = bbuf.array();

        // get a byte matrix for the data
        String data = new String(b, "ISO-8859-1");
        com.google.zxing.Writer writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(data, com.google.zxing.BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
        return baos.toByteArray();
    }


    @Override
    public String decode(InputStream stream) throws Exception {

        BufferedImage image = ImageIO.read(stream);
        if (image == null) throw new Exception("Could not decode image");
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        return String.valueOf(result.getText());
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setHandleDao(HandleDao handleDao) {
        this.handleDao = handleDao;
    }

    public void setMappingsService(MappingsService mappingsService) {
        this.mappingsService = mappingsService;
    }
}
