
package com.comerzzia.pos.devices.drivers.direct;

import java.util.HashMap;
import java.util.Map;

import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;


public class CodesDirect { //extends Codes
	
	public static final String TAG_COMMANDS = "commands";
	public static final String ATT_NAME = "name";
	public static final String ATT_VALUE = "value";
    
    public static String ENCODING = "ENCODING";
    public static String SELECT_PRINTER = "SELECT_PRINTER"; // {0x1B, 0x3D, 0x01} 27;61;1
    public static String CHAR_SIZE_0 = "CHAR_SIZE_0";
    public static String CHAR_SIZE_1 = "CHAR_SIZE_1";
    public static String CHAR_SIZE_2 = "CHAR_SIZE_2";
    public static String CHAR_SIZE_3 = "CHAR_SIZE_3";
    public static String CHAR_SIZE_CONDENSED = "CHAR_SIZE_CONDENSED";
    public static String BOLD_SET = "BOLD_SET";
    public static String BOLD_RESET = "BOLD_RESET";
    public static String UNDERLINE_SET = "UNDERLINE_SET";
    public static String UNDERLINE_RESET = "UNDERLINE_RESET";
    public static String OPEN_DRAWER = "OPEN_DRAWER";
    public static String PARTIAL_CUT = "PARTIAL_CUT"; // 27;64;29;86,1
    public static String IMAGE_HEADER = "IMAGE_HEADER";
    public static String NEW_LINE = "NEW_LINE"; // {0x0D, 0x0A}; // Print and carriage return  // 13;12
    public static String FONT_SELECT = "FONT_SELECT";
    public static String FONT_A = "FONT_A";
    public static String FONT_B = "FONT_B";
    public static String FONT_C = "FONT_D";
    public static String FONT_D = "FONT_E";
    
    public static String BAR_HEIGHT = "BAR_HEIGHT";
    public static String BAR_WIDTH = "BAR_WIDTH";
    public static String BAR_PSOITION_NONE = "BAR_PSOITION_NONE";
    public static String BAR_PSOITION_DOWN = "BAR_PSOITION_DOWN";
    public static String BAR_HR_FONT = "BAR_HR_FONT";
    
    public static String BAR_CODE_LENGHT = "BAR_CODE_LENGHT";
    public static String BAR_CODE_NUMBER = "BAR_CODE_NUMBER";
    public static String BAR_CODE_ALIGN_LEFT = "BAR_CODE_ALIGN_LEFT";
    public static String BAR_CODE_ALIGN_RIGHT = "BAR_CODE_ALIGN_RIGHT";
    public static String BAR_CODE_ALIGN_CENTER = "BAR_CODE_ALIGN_CENTER";
    public static String QR_CODE_PREFIX = "QR_CODE_PREFIX";
    
    public static String LEFT_MARGIN_CHARS = "LEFT_MARGIN_CHARS";
    public static String LEFT_MARGIN_DOTS = "LEFT_MARGIN_DOTS";
    public static String BOTTOM_MARGIN = "BOTTOM_MARGIN";
    
    public static String PRINT_LOGO = "PRINT_LOGO";
    
    // Mapa con los parametros de configuración de la impresora
    private Map<String,String> codes;      
    
    protected static final byte[] INITSEQUENCE = {};


    
    /** Crea una nueva instancia de CodesDirect */
    public CodesDirect() {
        codes = new HashMap<>();        
    }
    
//    
//    @Override
//    public byte[] getInitSequence() { return INITSEQUENCE; }
//         
    public String getSize0() { return codes.get(CHAR_SIZE_0); }
    public String getSize1() { return codes.get(CHAR_SIZE_1); }
    public String getSize2() { return codes.get(CHAR_SIZE_2); }  
    public String getSize3() { return codes.get(CHAR_SIZE_3); }
    
    public String getBoldSet() { return codes.get(BOLD_SET);  }   
    public String getBoldReset() { return codes.get(BOLD_RESET); }   
    public String getUnderlineSet() { return codes.get(UNDERLINE_SET); }   
    public String getUnderlineReset() { return codes.get(UNDERLINE_RESET); }  
    public String getOpenDrawer() { return codes.get(OPEN_DRAWER);  }    
    public String getCutReceipt() { return codes.get(PARTIAL_CUT); }      
    public String getNewLine() { return codes.get(NEW_LINE);  } 
    public String getImageHeader() { return codes.get(IMAGE_HEADER);  } 
    public int getImageWidth() { return 256; }    
    public String getSizeCondensed() { return codes.get(CHAR_SIZE_CONDENSED);  }
    
    public String getFontA() { return codes.get(FONT_A);  }
    public String getFontB() { return codes.get(FONT_B);  }
    public String getFontC() { return codes.get(FONT_C);  }
    public String getFontD() { return codes.get(FONT_D);  }
    public String getFontSelect() { return codes.get(FONT_SELECT);  }
    public String getSelectPrinter() { return codes.get(SELECT_PRINTER); }
    public String getBarHeight() { return codes.get(BAR_HEIGHT);  }
    public String getBarWidth() { return codes.get(BAR_WIDTH);  }
    public String getBarPositionNone() { return codes.get(BAR_PSOITION_NONE);  }
    public String getBarPositionDown() { return codes.get(BAR_PSOITION_DOWN);  }
    public String getBarHRFont() { return codes.get(BAR_HR_FONT);  }
    public String getBarCodeDataLength() { return codes.get(BAR_CODE_LENGHT);  }
    public String getLeftMarginCommand() { return  codes.get(LEFT_MARGIN_DOTS);  }        //"29;76";
    public String getBarCodeNumberCommand() { return codes.get(BAR_CODE_NUMBER);  }       //"29;72"
    public String getBarCodeAlignLeft() { return codes.get(BAR_CODE_ALIGN_LEFT);  }       //"27;97;0"
    public String getBarCodeAlignRight() { return codes.get(BAR_CODE_ALIGN_RIGHT);  }     //"27;97;2"
    public String getBarCodeAlignCenter() { return codes.get(BAR_CODE_ALIGN_CENTER);  }   //"27;97;1"
    public String getQRPrefix() { return  codes.get(QR_CODE_PREFIX);  }                   //"29;40;107"
    
    public String getPringLogo() { return  codes.get(PRINT_LOGO);  }                      // 28;121;n;0"
    
    public int getLeftMarginChars() { 
        String leftMarginChar = codes.get(LEFT_MARGIN_CHARS);
        if (leftMarginChar==null || leftMarginChar.isEmpty() ){
            return  0;  
        }
        else{
            return new Integer(leftMarginChar);
        }
    }
    
    public int getBottomMargin() { 
        String bottomMargin = codes.get(BOTTOM_MARGIN);
        if (bottomMargin==null || bottomMargin.isEmpty() ){
            return  0;  
        }
        else{
            return new Integer(bottomMargin);
        }
    }
    
      
    /**
     * @return the codigos
     */
    public Map<String,String> getCodes() {
        return codes;
    }

    /**
     * @param codigos the codigos to set
     */
    public void setCodes(Map<String,String> codigos) {
        this.codes = codigos;
    }

    public void configure(XMLDocumentNode config) {
        try {
            // Leemos la configuración
            XMLDocumentNode root = config;
            XMLDocumentNode properties = root.getNode(TAG_COMMANDS);
            
            for (XMLDocumentNode property : properties.getChildren()){
                String nompreProp = property.getAttributeValue(ATT_NAME, false);
                String valorProp = property.getAttributeValue(ATT_VALUE, false);
                codes.put(nompreProp, valorProp);
            }                
        }
        catch (XMLDocumentNodeNotFoundException ex) {
            
        }
    }
}

    

