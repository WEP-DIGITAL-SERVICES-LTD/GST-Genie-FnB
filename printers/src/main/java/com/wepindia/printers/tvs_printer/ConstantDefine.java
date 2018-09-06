package com.wepindia.printers.tvs_printer;

public class ConstantDefine {
	
	//����ֵ����
	public static final int ERR_SYSTEM_SELECT_PRINT_MODE = 1003;
	public static final int ERR_SYSTEM_SELECT_PAPER_TYPE = 1004;
	public static final int ERR_SYSTEM_SET_MOTION_UNIT = 1005;
	public static final int ERR_SYSTEM_QUERY_STATUS = 1006;
	public static final int ERR_SYSTEM_FEED_LINE = 1007;
	public static final int ERR_SYSTEM_CUT_PAPER = 1008;
	public static final int ERR_CASH_DRAWER_OPEN = 1009;
	public static final int ERR_TEXT_SELECT_CHAR_SET = 1010;
	public static final int ERR_TEXT_SELECT_CODE_PAGE = 1011;
	public static final int ERR_TEXT_SET_LINE_HEIGHT  = 1012;
	public static final int ERR_TEXT_SET_CHARACTER_SPACE = 1013;
	public static final int ERR_TEXT_STANDARD_MODE_ALIGNMENT = 1014;
	public static final int ERR_TEXT_SELECT_FONT_TYPE = 1015;
	public static final int ERR_TEXT_SET_FONT_STYLE_REVERSE = 1016;
	public static final int ERR_TEXT_SET_FONT_STYLE_SMOOTH = 1017;
	public static final int ERR_TEXT_SET_FONT_STYLE_BOLD = 1018;
	public static final int ERR_TEXT_SET_FONT_STYLE_UNDERLINE = 1019;
	public static final int ERR_TEXT_STANDARD_MODE_UPSIDEDOWN = 1020;
	public static final int ERR_TEXT_SELECT_MAGNIFY_TIMES = 1021;
	public static final int ERR_TEXT_STANDARD_MODE_ROTATE = 1022;
	public static final int ERR_TEXT_ENTER_QUIT_COLOR_PRINT = 1023;
	public static final int ERR_TEXT_SET_COLOR_PRINT = 1024;
	public static final int ERR_TEXT_FONT_USER_DEFINED_ENABLE = 1025;
	public static final int ERR_TEXT_FONT_USER_DEFINED = 1026;
	public static final int ERR_TEXT_FONT_USER_DEFINED_CANCEL = 1027;
	public static final int ERR_IMAGE_DOWNLOAD_AND_PRINT = 1028;
	public static final int ERR_IMAGE_DOWNLOAD_RAM = 1029;
	public static final int ERR_IMAGE_RAM_PRINT = 1030;
	public static final int ERR_IMAGE_DOWNLOAD_FLASH = 1031;
	public static final int ERR_IMAGE_FLASH_PRINT = 1032;
	public static final int ERR_IMAGE_STANDARD_MODE_RASTER_PRINT = 1033;
	public static final int ERR_STANDARD_MODE_SET_PRINTAREA_WIDTH = 1034;
	public static final int ERR_STANDARD_MODE_SET_LEFT_MARGIN = 1035;
	public static final int ERR_STANDARD_MODE_SET_HORIZONTAL_STARTING_POSITION = 1036;
	public static final int ERR_PAGE_MODE_SET_VERTICAL_STARTING_POSITION = 1037;
	public static final int ERR_PAGE_MODE_SET_PRINT_AREA = 1038;
	public static final int ERR_PAGE_MODE_SET_PRINT_DIRECTION = 1039;
	public static final int ERR_PAGE_MODE_PRINT = 1040;
	public static final int ERR_PAGE_MODE_CLEAR_BUFFER = 1041;
	public static final int ERR_BARCODE_PRINT_1D = 1042;
	public static final int ERR_BARCODE_PRINT_2D = 1043;
	public static final int ERR_BARCODE_SELECT_MODULE_WIDTH = 1044;
	public static final int ERR_BARCODE_SELECT_BARCODE_HEIGHT = 1045;
	public static final int ERR_BARCODE_SELECT_HRI_FONT_TYPE = 1046;
	public static final int ERR_BARCODE_SELECT_HRI_FONT_POSITION = 1047;
	public static final int ERR_BARCODE_QR_SET_PARAM = 1048;
	public static final int ERR_BARCODE_PDF417_SELECT_CORRECTION_GRADE = 1049;
	public static final int ERR_BARCODE_PDF417_SET_SIZE = 1050;
	public static final int ERR_BARCODE_GS1DATABAR_SET_PARAM = 1051;
	public static final int ERR_COMMUNICATE = 1052;
	
	//��ӡģʽ
	public static final int PRINT_MODE_STANDARD = 0;
	public static final int PRINT_MODE_PAGE=1;
	
	//��ֽģʽ
	public static final int CutFullImmdediately=0;
	public static final int CutPartImmdediately=1;
	public static final int CutPartAfterFeed=65;
	
	//�����ַ���
	public static final int CharacterSetUSA=0;
	public static final int CharacterSetFrance=1;
	public static final int CharacterSetGermany=2;
	public static final int CharacterSetUK=3;
	public static final int CharacterSetDenmark_I=4;
	public static final int CharacterSetSweden=5;
	public static final int CharacterSetItaly=6;
	public static final int CharacterSetSpain_I=7;
	public static final int CharacterSetJapan=8;
	public static final int CharacterSetNorway=9;
	public static final int CharacterSetDenmark_II=10;
	public static final int CharacterSetSpain_II=11;
	public static final int CharacterSetLatin_America=12;
	public static final int CharacterSetKorea=13;
	
	//�ַ�ģʽ 
	public static final int ChineseCharacterMode=0;
	public static final int EnglishCharacterMode=1;
	
	//��ӡ�ı����뷽ʽ
	public static final int TextAlignmentLeft=0;
	public static final int TextAlignmentCenter=1;
	public static final int TextAlignmentRight=2;
	
	//��ת����
	public static final int RotatePrintNormal=0;
	public static final int RotatePrintR90=1;
	public static final int RotatePrint180=2;
	public static final int RotatePrintL90=3;
	
	//��������
	public static final int FontTypeStandardASCII=0;
	public static final int FontTypeCompressedASCII=1;
	public static final int FontTypeUserDefined=2;
	public static final int FontTypeChinese=3;
	
	//������
	public static final int FontStyleReverse=0x400;
	public static final int FontStyleBold=0x08;
	public static final int FontStyleUpsideDown=0x200;
	public static final int FontStyleUnderlineOneDotThick=0x80;
	public static final int FontStyleUnderlineTwoDotThick=0x100;
	
	//ѡ���ȡ���Զ����ַ�
	public static final int FontUserDefinedDisable=0;
	public static final int FontUserDefinedEnable=1;
	
	//��ӡ����
	public static final int SingleDensity_8=0;
	public static final int DoubleDensity_8=1;
	public static final int SingleDensity_24=32;
	public static final int DoubleDensity_24=33;
	
	//ͼ���ӡģʽ
	public static final int NormalMode=0;
	public static final int Double_width=1;
	public static final int Double_height=2;
	public static final int Quadruple=3;
	
	//һά��������
	public static final int BarcodeUPC_A=65;
	public static final int BarcodeUPC_E=66;
	public static final int BarcodeJAN13orEAN13=67;
	public static final int BarcodeJAN8orEAN8=68;
	public static final int BarcodeCODE39=69;
	public static final int BarcodeITF=70;
	public static final int BarcodeCODABAR=71;
	public static final int BarcodeCODE93=72;
	public static final int BarcodeCODE128=73;
	
	//����ģʽ
	public static final int LanguageChinese=0;
	public static final int LanguageJapanese=1;
	
	//GS1��������
	public static final int GS1DataBarOmnidirectional=1;
	public static final int GS1DataBarTruncated=2;
	public static final int GS1DataBarStacked=3;
	public static final int GS1DataBarStackedOmnidirectional=4;
	public static final int GS1DataBarLimited=5;
	public static final int GS1DataBarExpanded=6;
	public static final int GS1DataBarExpandedStacked=7;
	
	//��ӡ����
	public static final int LeftToRight=0;
	public static final int BottomToTop=1;
	public static final int RightToLeft=2;
	public static final int TopToBottom=3;
	
	//����HRI�ַ���ӡλ��
	public static final int HRINone = 0;
	public static final int HRIAbove = 1;
	public static final int HRIBelow = 2;
	public static final int HRIAboveAndBelow = 3;
}
