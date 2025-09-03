package com.comerzzia.iskaypet.pos.devices.impresora;

import com.comerzzia.iskaypet.pos.devices.drivers.IskaypetCodesDirect;
import com.comerzzia.pos.dispositivo.impresora.ImpresoraDriver;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class IskaypetImpresoraDriver extends ImpresoraDriver {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ImpresoraDriver.class.getName());

    @Override
    public void establecerComandos() {
        this.codigosImpresora = new IskaypetCodesDirect();
        this.codigosImpresora.configurar(this.getConfiguracion().getConfiguracionModelo().getConfigModelo());
    }

    @Override
    public void imprimirLogo() {
        try {

            Sesion sesion = SpringContext.getBean(Sesion.class);

            String uidActividad = sesion.getAplicacion().getUidActividad();
            String codEmpresa = sesion.getAplicacion().getEmpresa().getCodEmpresa();

            // Carga el logo de la empresa
            InputStream input = this.getClass().getClassLoader().getResourceAsStream("logos/" + uidActividad + "_" + codEmpresa + "_logo.png");

            // Carga el logo de la actividad si no se ha encontrado el de la empresa
            if (input == null) {
                input = this.getClass().getClassLoader().getResourceAsStream("logos/" + uidActividad + "_logo.png");
            }

            // Carga el logo por defecto si no se ha encontrado el de la actividad
            if (input == null) {
                input = this.getClass().getClassLoader().getResourceAsStream("logos/logo.png");
            }

            if (input == null) {
                throw new RuntimeException("No se encontró el recurso: logos/logo.png");
            }

            BufferedImage image = ImageIO.read(input);

            if (image == null) {
                throw new IOException("No se pudo leer la imagen");
            }

            BufferedImage monochromeImage = convertToMonochrome(image);
            int[][] pixels = getPixelsSlow(monochromeImage);
            printImage(pixels);

            ejecutaComando(codigosImpresora.getNewLine());

        } catch (Exception e) {
            log.error("Ha ocurrido un error imprimiendo el logo: " + e.getMessage(), e);
        }

    }

    private void printImage(int[][] pixels) throws IOException {
        ejecutaComando(((IskaypetCodesDirect) codigosImpresora).getImageAlignCenter());
        ejecutaComando(((IskaypetCodesDirect) codigosImpresora).getLineSpace24());
        for (int y = 0; y < pixels.length; y += 24) {
            ejecutaComando(((IskaypetCodesDirect) codigosImpresora).getImageBitMode());
            streamOut.write(new byte[]{(byte) (0x00ff & pixels[y].length)
                    , (byte) ((0xff00 & pixels[y].length) >> 8)});
            for (int x = 0; x < pixels[y].length; x++) {
                streamOut.write(recollectSlice(y, x, pixels));
            }
            ejecutaComando(codigosImpresora.getNewLine());
        }

        ejecutaComando(((IskaypetCodesDirect) codigosImpresora).getLineSpace30());
    }

    private static BufferedImage convertToMonochrome(BufferedImage image) {
        BufferedImage monochromeImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        monochromeImage.getGraphics().drawImage(image, 0, 0, null);
        return monochromeImage;
    }

    private int[][] getPixelsSlow(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getRGB(col, row);
            }
        }

        return result;
    }

    private byte[] recollectSlice(int y, int x, int[][] img) {
        byte[] slices = new byte[]{0, 0, 0};
        for (int yy = y, i = 0; yy < y + 24 && i < 3; yy += 8, i++) {
            byte slice = 0;
            for (int b = 0; b < 8; b++) {
                int yyy = yy + b;
                if (yyy >= img.length) {
                    continue;
                }
                int col = img[yyy][x];
                boolean v = shouldPrintColor(col);
                slice |= (byte) ((v ? 1 : 0) << (7 - b));
            }
            slices[i] = slice;
        }

        return slices;
    }

    private boolean shouldPrintColor(int col) {
        final int threshold = 127;
        int a, r, g, b, luminance;
        a = (col >> 24) & 0xff;
        if (a != 0xff) {// Ignore transparencies
            return false;
        }
        r = (col >> 16) & 0xff;
        g = (col >> 8) & 0xff;
        b = col & 0xff;

        luminance = (int) (0.299 * r + 0.587 * g + 0.114 * b);

        return luminance < threshold;
    }
}
