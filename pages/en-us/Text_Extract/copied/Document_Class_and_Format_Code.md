---
layout: default
title: Document Class & Format Code Reference
---

## Document Class & Format Code Reference

This page serves as a reference for the DOC_FORMAT_CODE & DOC_CLASS_CODE fields extracted by the Text Extract Worker. This information is taken from the Keyview 11 Filter SDK Java Programming Guide.

Note: This is not a complete list of file extensions. KeyView returns format codes based on file
content, which cannot always be predicted from the file extension. Some file extensions may also
be associated with multiple format numbers.

### Document Class Codes 
#### (DOC\_CLASS\_CODE)

| Code | Description |
|------|-------------|
| -1 | Error Occurred |
| 0 | No format found |
| 1 | Word Processor Document |
| 2 | Spreadsheet Document |
| 3 | Database Document |
| 4 | Raster image Document |
| 5 | Vector graphic Document |
| 6 | Presentation Document |
| 7 | Executable File |
| 8 | Encapsulation Format |
| 9 | Sound File Format |
| 10 | Desktop Publishing |
| 11 | Planning/Outline Format |
| 12 | General Purpose Document |
| 13 | Mixed Type Document |
| 14 | Font Type Document |
| 15 | Scheduling/Planning Format |
| 16 | Communications Format |
| 17 | Object Module Format |
| 18 | Library Format |
| 19 | FAX Format |
| 20 | Movie File |
| 21 | Animation File |

### Document Format Codes 
#### (DOC\_FORMAT\_CODE)

| Format Name | Format Number | Format Description | Associated FileExtension |
|-------------|---------------|--------------------|--------------------------|
| AES_Multiplus_Comm_Fmt | 1 | Multiplus (AES) | PTF |
| ASCII_Text_Fmt | 2 | Text |   |
| MSDOS_Batch_File_Fmt | 3 | MS-DOS Batch File | BAT |
| Applix_Alis_Fmt | 4 | APPLIX ASTERIX | AX |
| BMP_Fmt | 5 | Windows Bitmap | BMP |
| CT_DEF_Fmt | 6 | Convergent Technologies DEF Comm. Format |   |
| Corel_Draw_Fmt | 7 | Corel Draw | CDR |
| CGM_ClearText_Fmt | 8 | Computer Graphics Metafile(CGM) | CGM1 |
| CGM_Binary_Fmt | 9 | Computer Graphics Metafile(CGM) | CGM 1 |
| CGM_Character_Fmt | 10 | Computer Graphics Metafile(CGM) | CGM 1 |
| Word_Connection_Fmt | 11 | Word Connection | CN |
| COMET_TOP_Word_Fmt | 12 | COMET TOP |   |
| CEOwrite_Fmt | 13 | CEOwrite | CW |
| DSA101_Fmt | 14 | DSA101 (Honeywell Bull) |   |
| DCA_RFT_Fmt | 15 | DCA-RFT (IBM Revisable Form) | RFT |
| CDA_DDIF_Fmt | 16 | CDA / DDIF |   |
| DG_CDS_Fmt | 17 | DG Common Data Stream (CDS) | CDS |
| Micrografx_Draw_Fmt | 18 | Windows Draw (Micrografx) | DRW |
| Data_Point_VistaWord_Fmt | 19 | Vistaword |   |
| DECdx_Fmt | 20 | DECdx | DX |
| Enable_WP_Fmt | 21 | Enable Word Processing | WPF |
| EPSF_Fmt | 22 | Encapsulated PostScript | EPS 1 |
| Preview_EPSF_Fmt | 23 | Encapsulated PostScript | EPS 1 |
| MS_Executable_Fmt | 24 | MSDOS/Windows Program | EXE |
| G31D_Fmt | 25 | CCITT G3 1D |   |
| GIF_87a_Fmt | 26 | Graphics Interchange Format (GIF87a) | GIF 1 |
| GIF_89a_Fmt | 27 | Graphics Interchange Format (GIF89a) | GIF 1 |
| HP_Word_PC_Fmt | 28 | HP Word PC | HW |
| IBM_1403_LinePrinter_Fmt | 29 | IBM 1403 Line Printer | I4 |
| IBM_DCF_Script_Fmt | 30 | DCF Script | IC |
| IBM_DCA_FFT_Fmt | 31 | DCA-FFT (IBM Final Form) | IF |
| Interleaf_Fmt | 32 | Interleaf |   |
| GEM_Image_Fmt | 33 | GEM Bit Image | IMG |
| IBM_Display_Write_Fmt | 34 | Display Write | IP |
| Sun_Raster_Fmt | 35 | Sun Raster | RAS |
| Ami_Pro_Fmt | 36 | Lotus Ami Pro | SAM |
| Ami_Pro_StyleSheet_Fmt | 37 | Lotus Ami Pro Style Sheet |   |
| MORE_Fmt | 38 | MORE Database MAC |   |
| Lyrix_Fmt | 39 | Lyrix Word Processing |   |
| MASS_11_Fmt | 40 | MASS-11 | M1 |
| MacPaint_Fmt | 41 | MacPaint | PNTG |
| MS_Word_Mac_Fmt | 42 | Microsoft Word for Macintosh | DOC 1 |
| SmartWare_II_Comm_Fmt | 43 | SmartWare II |   |
| MS_Word_Win_Fmt | 44 | Microsoft Word for Windows | DOC 1 |
| Multimate_Fmt | 45 | MultiMate | MM 1 |
| Multimate_Fnote_Fmt | 46 | MultiMate Footnote File | FNX 1 |
| Multimate_Adv_Fmt | 47 | MultiMate Advantage |   |
| Multimate_Adv_Fnote_Fmt | 48 | MultiMate Advantage Footnote File |   |
| Multimate_Adv_II_Fmt | 49 | MultiMate Advantage II | MM1 |
| Multimate_Adv_II_Fnote_Fmt | 50 | MultiMate Advantage II Footnote File | FNX 1 |
| Multiplan_PC_Fmt | 51 | Multiplan (PC) |   |
| Multiplan_Mac_Fmt | 52 | Multiplan (Mac) |   |
| MS_RTF_Fmt | 53 | Rich Text Format (RTF) | RTF |
| MS_Word_PC_Fmt | 54 | Microsoft Word for PC | DOC 1 |
| MS_Word_PC_StyleSheet_Fmt | 55 | Microsoft Word for PC Style Sheet | DOC 1 |
| MS_Word_PC_Glossary_Fmt | 56 | Microsoft Word for PC Glossary | DOC 1 |
| MS_Word_PC_Driver_Fmt | 57 | Microsoft Word for PC Driver | DOC 1 |
| MS_Word_PC_Misc_Fmt | 58 | Microsoft Word for PC Miscellaneous File | DOC 1 |
| NBI_Async_Archive_Fmt | 59 | NBI Async Archive Format |   |
| Navy_DIF_Fmt | 60 | Navy DIF | ND |
| NBI_Net_Archive_Fmt | 61 | NBI Net Archive Format | NN |
| NIOS_TOP_Fmt | 62 | NIOS TOP |   |
| FileMaker_Mac_Fmt | 63 | Filemaker MAC | FP5, FP7 |
| ODA_Q1_11_Fmt | 64 | ODA / ODIF | OD1 |
| ODA_Q1_12_Fmt | 65 | ODA / ODIF | OD 1 |
| OLIDIF_Fmt | 66 | OLIDIF (Olivetti) |   |
| Office_Writer_Fmt | 67 | Office Writer | OW |
| PC_Paintbrush_Fmt | 68 | PC Paintbrush Graphics (PCX) | PCX |
| CPT_Comm_Fmt | 69 | CPT |   |
| Lotus_PIC_Fmt | 70 | Lotus PIC | PIC |
| Mac_PICT_Fmt | 71 | QuickDraw Picture | PCT |
| Philips_Script_Word_Fmt | 72 | Philips Script |   |
| PostScript_Fmt | 73 | PostScript | PS |
| PRIMEWORD_Fmt | 74 | PRIMEWORD |   |
| Quadratron_Q_One_v1_Fmt | 75 | Q-One V1.93J | Q1 1, QX 1 |
| Quadratron_Q_One_v2_Fmt | 76 | Q-One V2.0 | Q1 1, QX 1 |
| SAMNA_Word_IV_Fmt | 77 | SAMNA Word | SAM |
| Ami_Pro_Draw_Fmt | 78 | Lotus Ami Pro Draw | SDW |
| SYLK_Spreadsheet_Fmt | 79 | SYLK |   |
| SmartWare_II_WP_Fmt | 80 | SmartWare II |   |
| Symphony_Fmt | 81 | Symphony | WR1 |
| Targa_Fmt | 82 | Targa | TGA |
| TIFF_Fmt | 83 | TIFF | TIF, TIFF |
| Targon_Word_Fmt | 84 | Targon Word | TW |
| Uniplex_Ucalc_Fmt | 85 | Uniplex Ucalc | SS |
| Uniplex_WP_Fmt | 86 | Uniplex | UP |
| MS_Word_UNIX_Fmt | 87 | Microsoft Word UNIX | DOC1 |
| WANG_PC_Fmt | 88 | WANG PC |   |
| WordERA_Fmt | 89 | WordERA |   |
| WANG_WPS_Comm_Fmt | 90 | WANG WPS | WF |
| WordPerfect_Mac_Fmt | 91 | WordPerfect MAC | WPM, WPD1 |
| WordPerfect_Fmt | 92 | WordPerfect | WO, WPD1 |
| WordPerfect_VAX_Fmt | 93 | WordPerfect VAX | WPD1 |
| WordPerfect_Macro_Fmt | 94 | WordPerfect Macro |   |
| WordPerfect_Dictionary_Fmt | 95 | WordPerfect Spelling Dictionary |   |
| WordPerfect_Thesaurus_Fmt | 96 | WordPerfect Thesaurus |   |
| WordPerfect_Resource_Fmt | 97 | WordPerfect Resource File |   |
| WordPerfect_Driver_Fmt | 98 | WordPerfect Driver |   |
| WordPerfect_Cfg_Fmt | 99 | WordPerfect ConfigurationFile |   |
| WordPerfect_Hyphenation_Fmt | 100 | WordPerfect Hyphenation Dictionary |   |
| WordPerfect_Misc_Fmt | 101 | WordPerfect MiscellaneousFile | WPD1 |
| WordMARC_Fmt | 102 | WordMARC | WM, PW |
| Windows_Metafile_Fmt | 103 | Windows Metafile | WMF1 |
| Windows_Metafile_NoHdr_Fmt | 104 | Windows Metafile (no header) | WMF1 |
| SmartWare_II_DB_Fmt | 105 | SmartWare II |   |
| WordPerfect_Graphics_Fmt | 106 | WordPerfect Graphics | WPG, QPG |
| WordStar_Fmt | 107 | WordStar | WS |
| WANG_WITA_Fmt | 108 | WANG WITA | WT |
| Xerox_860_Comm_Fmt | 109 | Xerox 860 |   |
| Xerox_Writer_Fmt | 110 | Xerox Writer |   |
| DIF_SpreadSheet_Fmt | 111 | Data Interchange Format (DIF) | DIF |
| Enable_Spreadsheet_Fmt | 112 | Enable Spreadsheet | SSF |
| SuperCalc_Fmt | 113 | Supercalc | CAL |
| UltraCalc_Fmt | 114 | UltraCalc |   |
| SmartWare_II_SS_Fmt | 115 | SmartWare II |   |
| SOF_Encapsulation_Fmt | 116 | Serialized Object Format (SOF) | SOF |
| PowerPoint_Win_Fmt | 117 | PowerPoint PC | PPT1 |
| PowerPoint_Mac_Fmt | 118 | PowerPoint MAC | PPT1 |
| PowerPoint_95_Fmt | 119 | PowerPoint 95 | PPT1 |
| PowerPoint_97_Fmt | 120 | PowerPoint 97 | PPT1 |
| PageMaker_Mac_Fmt | 121 | PageMaker for Macintosh |   |
| PageMaker_Win_Fmt | 122 | PageMaker for Windows |   |
| MS_Works_Mac_WP_Fmt | 123 | Microsoft Works for MAC |   |
| MS_Works_Mac_DB_Fmt | 124 | Microsoft Works for MAC |   |
| MS_Works_Mac_SS_Fmt | 125 | Microsoft Works for MAC |   |
| MS_Works_Mac_Comm_Fmt | 126 | Microsoft Works for MAC |   |
| MS_Works_DOS_WP_Fmt | 127 | Microsoft Works for DOS | WPS1 |
| MS_Works_DOS_DB_Fmt | 128 | Microsoft Works for DOS | WDB1 |
| MS_Works_DOS_SS_Fmt | 129 | Microsoft Works for DOS |   |
| MS_Works_Win_WP_Fmt | 130 | Microsoft Works for Windows | WPS1 |
| MS_Works_Win_DB_Fmt | 131 | Microsoft Works for Windows | WDB1 |
| MS_Works_Win_SS_Fmt | 132 | Microsoft Works for Windows | S30, S40 |
| PC_Library_Fmt | 133 | DOS/Windows Object Library |   |
| MacWrite_Fmt | 134 | MacWrite |   |
| MacWrite_II_Fmt | 135 | MacWrite II |   |
| Freehand_Fmt | 136 | Freehand MAC |   |
| Disk_Doubler_Fmt | 137 | Disk Doubler |   |
| HP_GL_Fmt | 138 | HP Graphics Language | HPGL |
| FrameMaker_Fmt | 139 | FrameMaker | FM, FRM |
| FrameMaker_Book_Fmt | 140 | FrameMaker | BOOK |
| Maker_Markup_Language_Fmt | 141 | Maker Markup Language |   |
| Maker_Interchange_Fmt | 142 | Maker Interchange Format (MIF) | MIF |
| JPEG_File_Interchange_Fmt | 143 | Interchange Format | JPG, JPEG |
| Reflex_Fmt | 144 | Reflex |   |
| Framework_Fmt | 145 | Framework |   |
| Framework_II_Fmt | 146 | Framework II | FW3 |
| Paradox_Fmt | 147 | Paradox | DB |
| MS_Windows_Write_Fmt | 148 | Windows Write | WRI |
| Quattro_Pro_DOS_Fmt | 149 | Quattro Pro for DOS |   |
| Quattro_Pro_Win_Fmt | 150 | Quattro Pro for Windows | WB2, WB3 |
| Persuasion_Fmt | 151 | Persuasion |   |
| Windows_Icon_Fmt | 152 | Windows Icon Format | ICO |
| Windows_Cursor_Fmt | 153 | Windows Cursor | CUR |
| MS_Project_Activity_Fmt | 154 | Microsoft Project | MPP1 |
| MS_Project_Resource_Fmt | 155 | Microsoft Project | MPP1 |
| MS_Project_Calc_Fmt | 156 | Microsoft Project | MPP1 |
| PKZIP_Fmt | 157 | ZIP Archive | ZIP |
| Quark_Xpress_Fmt | 158 | Quark Xpress MAC |   |
| ARC_PAK_Archive_Fmt | 159 | PAK/ARC Archive | ARC, PAK |
| MS_Publisher_Fmt | 160 | Microsoft Publisher | PUB1 |
| PlanPerfect_Fmt | 161 | PlanPerfect |   |
| WordPerfect_Auxiliary_Fmt | 162 | WordPerfect auxiliary file | WPW |
| MS_WAVE_Audio_Fmt | 163 | Microsoft Wave | WAV |
| MIDI_Audio_Fmt | 164 | MIDI | MID, MIDI |
| AutoCAD_DXF_Binary_Fmt | 165 | AutoCAD DXF | DXF1 |
| AutoCAD_DXF_Text_Fmt | 166 | AutoCAD DXF | DXF1 |
| dBase_Fmt | 167 | dBase | DBF |
| OS_2_PM_Metafile_Fmt | 168 | OS/2 PM Metafile | MET |
| Lasergraphics_Language_Fmt | 169 | Lasergraphics Language |   |
| AutoShade_Rendering_Fmt | 170 | AutoShade Rendering |   |
| GEM_VDI_Fmt | 171 | GEM VDI | VDI |
| Windows_Help_Fmt | 172 | Windows Help File | HLP |
| Volkswriter_Fmt | 173 | Volkswriter | VW4 |
| Ability_WP_Fmt | 174 | Ability |   |
| Ability_DB_Fmt | 175 | Ability |   |
| Ability_SS_Fmt | 176 | Ability |   |
| Ability_Comm_Fmt | 177 | Ability |   |
| Ability_Image_Fmt | 178 | Ability |   |
| XyWrite_Fmt | 179 | XYWrite / Nota Bene | XY4 |
| CSV_Fmt | 180 | CSV (Comma Separated Values) | CSV |
| IBM_Writing_Assistant_Fmt | 181 | IBM Writing Assistant | IWA |
| WordStar_2000_Fmt | 182 | WordStar 2000 | WS2 |
| HP_PCL_Fmt | 183 | HP Printer Control Language | PCL |
| UNIX_Exe_PreSysV_VAX_Fmt | 184 | Unix Executable (PDP-11/pre-System V VAX) |   |
| UNIX_Exe_Basic_16_Fmt | 185 | Unix Executable (Basic-16) |   |
| UNIX_Exe_x86_Fmt | 186 | Unix Executable (x86) |   |
| UNIX_Exe_iAPX_286_Fmt | 187 | Unix Executable (iAPX 286) |   |
| UNIX_Exe_MC68k_Fmt | 188 | Unix Executable (MC680x0) |   |
| UNIX_Exe_3B20_Fmt | 189 | Unix Executable (3B20) |   |
| UNIX_Exe_WE32000_Fmt | 190 | Unix Executable (WE32000) |   |
| UNIX_Exe_VAX_Fmt | 191 | Unix Executable (VAX) |   |
| UNIX_Exe_Bell_5_Fmt | 192 | Unix Executable (Bell 5.0) |   |
| UNIX_Obj_VAX_Demand_Fmt | 193 | Unix Object Module (VAX Demand) |   |
| UNIX_Obj_MS8086_Fmt | 194 | Unix Object Module (old MS 8086) |   |
| UNIX_Obj_Z8000_Fmt | 195 | Unix Object Module (Z8000) |   |
| AU_Audio_Fmt | 196 | NeXT/Sun Audio Data | AU |
| NeWS_Font_Fmt | 197 | NeWS bitmap font |   |
| cpio_Archive_CRChdr_Fmt | 198 | cpio archive (CRC Header) |   |
| cpio_Archive_CHRhdr_Fmt | 199 | cpio archive (CHR Header) |   |
| PEX_Binary_Archive_Fmt | 200 | SUN PEX Binary Archive |   |
| Sun_vfont_Fmt | 201 | SUN vfont Definition |   |
| Curses_Screen_Fmt | 202 | Curses Screen Image |   |
| UUEncoded_Fmt | 203 | UU encoded | UUE |
| WriteNow_Fmt | 204 | WriteNow MAC |   |
| PC_Obj_Fmt | 205 | DOS/Windows Object Module |   |
| Windows_Group_Fmt | 206 | Windows Group |   |
| TrueType_Font_Fmt | 207 | TrueType Font | TTF |
| Windows_PIF_Fmt | 208 | Program Information File(PIF) | PIF |
| MS_COM_Executable_Fmt | 209 | PC (.COM) | COM |
| StuffIt_Fmt | 210 | StuffIt (MAC) | HQX |
| PeachCalc_Fmt | 211 | PeachCalc |   |
| Wang_GDL_Fmt | 212 | WANG Office GDL Header |   |
| Q_A_DOS_Fmt | 213 | Q & A for DOS |   |
| Q_A_Win_Fmt | 214 | Q & A for Windows | JW |
| WPS_PLUS_Fmt | 215 | WPS-PLUS | WPL |
| DCX_Fmt | 216 | DCX FAX Format(PCX images | DCX |
| OLE_Fmt | 217 | OLE Compound Document | OLE |
| EBCDIC_Fmt | 218 | EBCDIC Text |   |
| DCS_Fmt | 219 | DCS |   |
| UNIX_SHAR_Fmt | 220 | SHAR | SHAR |
| Lotus_Notes_BitMap_Fmt | 221 | Lotus Notes Bitmap |   |
| Lotus_Notes_CDF_Fmt | 222 | Lotus Notes CDF | CDF |
| Compress_Fmt | 223 | Unix Compress | Z |
| GZ_Compress_Fmt | 224 | GZ Compress | GZ1 |
| TAR_Fmt | 225 | TAR | TAR |
| ODIF_FOD26_Fmt | 226 | ODA / ODIF | F26 |
| ODIF_FOD36_Fmt | 227 | ODA / ODIF | F36 |
| ALIS_Fmt | 228 | ALIS |   |
| Envoy_Fmt | 229 | Envoy | EVY |
| PDF_Fmt | 230 | Portable Document Format | PDF |
| BinHex_Fmt | 231 | BinHex | HQX |
| SMTP_Fmt | 232 | SMTP | SMTP |
| MIME_Fmt | 233 | MIME2 | EML, MBX |
| USENET_Fmt | 234 | USENET |   |
| SGML_Fmt | 235 | SGML | SGML |
| HTML_Fmt | 236 | HTML | HTM1, HTML 1 |
| ACT_Fmt | 237 | ACT | ACT |
| PNG_Fmt | 238 | Portable Network Graphics (PNG) | PNG |
| MS_Video_Fmt | 239 | Video for Windows (AVI) | AVI |
| Windows_Animated_Cursor_Fmt | 240 | Windows Animated Cursor | ANI |
| Windows_CPP_Obj_Storage_Fmt | 241 | Windows C++ Object Storage |   |
| Windows_Palette_Fmt | 242 | Windows Palette | PAL |
| RIFF_DIB_Fmt | 243 | RIFF Device Independent Bitmap |   |
| RIFF_MIDI_Fmt | 244 | RIFF MIDI | RMI |
| RIFF_Multimedia_Movie_Fmt | 245 | RIFF Multimedia Movie |   |
| MPEG_Fmt | 246 | MPEG Movie | MPG, MPEG1 |
| QuickTime_Fmt | 247 | QuickTime Movie, MPEG-4 Audio | MOV, QT, MP4 |
| AIFF_Fmt | 248 | Audio Interchange FileFormat (AIFF) | AIF, AIFF |
| Amiga_MOD_Fmt | 249 | Amiga MOD | MOD |
| Amiga_IFF_8SVX_Fmt | 250 | Amiga IFF (8SVX) Sound | IFF |
| Creative_Voice_Audio_Fmt | 251 | Creative Voice (VOC) | VOC |
| AutoDesk_Animator_FLI_Fmt | 252 | AutoDesk Animator FLIC | FLI |
| AutoDesk_AnimatorPro_FLC_Fmt | 253 | AutoDesk Animator Pro FLIC | FLC |
| Compactor_Archive_Fmt | 254 | Compactor / Compact Pro |   |
| VRML_Fmt | 255 | VRML | WRL |
| QuickDraw_3D_Metafile_Fmt | 256 | QuickDraw 3D Metafile |   |
| PGP_Secret_Keyring_Fmt | 257 | PGP Secret Keyring |   |
| PGP_Public_Keyring_Fmt | 258 | PGP Public Keyring |   |
| PGP_Encrypted_Data_Fmt | 259 | PGP Encrypted Data |   |
| PGP_Signed_Data_Fmt | 260 | PGP Signed Data |   |
| PGP_SignedEncrypted_Data_Fmt | 261 | PGP Signed and Encrypted Data |   |
| PGP_Sign_Certificate_Fmt | 262 | PGP Signature Certificate |   |
| PGP_Compressed_Data_Fmt | 263 | PGP Compressed Data |   |
| PGP_ASCII_Public_Keyring_Fmt | 264 | ASCII-armored PGP Public Keyring |   |
| PGP_ASCII_Encoded_Fmt | 265 | ASCII-armored PGP encoded | PGP1 |
| PGP_ASCII_Signed_Fmt | 266 | ASCII-armored PGP encoded | PGP1 |
| OLE_DIB_Fmt | 267 | OLE DIB object |   |
| SGI_Image_Fmt | 268 | SGI Image | RGB |
| Lotus_ScreenCam_Fmt | 269 | Lotus ScreenCam |   |
| MPEG_Audio_Fmt | 270 | MPEG Audio | MPEGA |
| FTP_Software_Session_Fmt | 271 | FTP Session Data | STE |
| Netscape_Bookmark_File_Fmt | 272 | Netscape Bookmark File | HTM1 |
| Corel_Draw_CMX_Fmt | 273 | Corel CMX | CMX |
| AutoDesk_DWG_Fmt | 274 | AutoDesk Drawing (DWG) | DWG |
| AutoDesk_WHIP_Fmt | 275 | AutoDesk WHIP | WHP |
| Macromedia_Director_Fmt | 276 | Macromedia Director | DCR |
| Real_Audio_Fmt | 277 | Real Audio | RM |
| MSDOS_Device_Driver_Fmt | 278 | MSDOS Device Driver | SYS |
| Micrografx_Designer_Fmt | 279 | Micrografx Designer | DSF |
| SVF_Fmt | 280 | Simple Vector Format (SVF) | SVF |
| Applix_Words_Fmt | 281 | Applix Words | AW |
| Applix_Graphics_Fmt | 282 | Applix Graphics | AG |
| MS_Access_Fmt | 283 | Microsoft Access | MDB1 |
| MS_Access_95_Fmt | 284 | Microsoft Access 95 | MDB1 |
| MS_Access_97_Fmt | 285 | Microsoft Access 97 | MDB1 |
| MacBinary_Fmt | 286 | MacBinary | BIN |
| Apple_Single_Fmt | 287 | Apple Single |   |
| Apple_Double_Fmt | 288 | Apple Double |   |
| Enhanced_Metafile_Fmt | 289 | Enhanced Metafile | EMF |
| MS_Office_Drawing_Fmt | 290 | Microsoft Office Drawing |   |
| XML_Fmt | 291 | XML | XML1 |
| DeVice_Independent_Fmt | 292 | DeVice Independent file(DVI) | DVI |
| Unicode_Fmt | 293 | Unicode | UNI |
| Lotus_123_Worksheet_Fmt | 294 | Lotus 1-2-3 | WK11 |
| Lotus_123_Format_Fmt | 295 | Lotus 1-2-3 Formatting | FM3 |
| Lotus_123_97_Fmt | 296 | Lotus 1-2-3 97 | WK11 |
| Lotus_Word_Pro_96_Fmt | 297 | Lotus Word Pro 96 | LWP1 |
| Lotus_Word_Pro_97_Fmt | 298 | Lotus Word Pro 97 | LWP1 |
| Freelance_DOS_Fmt | 299 | Lotus Freelance for DOS |   |
| Freelance_Win_Fmt | 300 | Lotus Freelance for Windows | PRE |
| Freelance_OS2_Fmt | 301 | Lotus Freelance for OS/2 | PRS |
| Freelance_96_Fmt | 302 | Lotus Freelance 96 | PRZ1 |
| Freelance_97_Fmt | 303 | Lotus Freelance 97 | PRZ1 |
| MS_Word_95_Fmt | 304 | Microsoft Word 95 | DOC1 |
| MS_Word_97_Fmt | 305 | Microsoft Word 97 | DOC1 |
| Excel_Fmt | 306 | Microsoft Excel | XLS1 |
| Excel_Chart_Fmt | 307 | Microsoft Excel | XLS1 |
| Excel_Macro_Fmt | 308 | Microsoft Excel | XLS1 |
| Excel_95_Fmt | 309 | Microsoft Excel 95 | XLS1 |
| Excel_97_Fmt | 310 | Microsoft Excel 97 | XLS1 |
| Corel_Presentations_Fmt | 311 | Corel Presentations | XFD, XFDL |
| Harvard_Graphics_Fmt | 312 | Harvard Graphics |   |
| Harvard_Graphics_Chart_Fmt | 313 | Harvard Graphics Chart | CH3, CHT |
| Harvard_Graphics_Symbol_Fmt | 314 | Harvard Graphics SymbolFile | SY3 |
| Harvard_Graphics_Cfg_Fmt | 315 | Harvard Graphics Configuration File |   |
| Harvard_Graphics_Palette_Fmt | 316 | Harvard Graphics Palette |   |
| Lotus_123_R9_Fmt | 317 | Lotus 1-2-3 Release 9 |   |
| Applix_Spreadsheets_Fmt | 318 | Applix Spreadsheets | AS |
| MS_Pocket_Word_Fmt | 319 | Microsoft Pocket Word | PWD, DOC1 |
| MS_DIB_Fmt | 320 | MS Windows Device Independent Bitmap |   |
| MS_Word_2000_Fmt | 321 | Microsoft Word 2000 | DOC1 |
| Excel_2000_Fmt | 322 | Microsoft Excel 2000 | XLS1 |
| PowerPoint_2000_Fmt | 323 | Microsoft PowerPoint 2000 | PPT |
| MS_Access_2000_Fmt | 324 | Microsoft Access 2000 | MDB1, MPP1 |
| MS_Project_4_Fmt | 325 | Microsoft Project 4 | MPP1 |
| MS_Project_41_Fmt | 326 | Microsoft Project 4.1 | MPP1 |
| MS_Project_98_Fmt | 327 | Microsoft Project 98 | MPP1 |
| Folio_Flat_Fmt | 328 | Folio Flat File | FFF |
| HWP_Fmt | 329 | HWP(Arae-Ah Hangul) | HWP |
| ICHITARO_Fmt | 330 | ICHITARO V4-10 |   |
| IS_XML_Fmt | 331 | Extended or Custom XML | XML1 |
| Oasys_Fmt | 332 | Oasys format | OA2, OA3 |
| PBM_ASC_Fmt | 333 | Portable Bitmap Utilities ASCII Format |   |
| PBM_BIN_Fmt | 334 | Portable Bitmap Utilities Binary Format |   |
| PGM_ASC_Fmt | 335 | Portable Greymap Utilities ASCII Format |   |
| PGM_BIN_Fmt | 336 | Portable Greymap Utilities Binary Format | PGM |
| PPM_ASC_Fmt | 337 | Portable Pixmap Utilities ASCII Format |   |
| PPM_BIN_Fmt | 338 | Portable Pixmap Utilities Binary Format |   |
| XBM_Fmt | 339 | X Bitmap Format | XBM |
| XPM_Fmt | 340 | X Pixmap Format | XPM |
| FPX_Fmt | 341 | FPX Format | FPX |
| PCD_Fmt | 342 | PCD Format | PCD |
| MS_Visio_Fmt | 343 | Microsoft Visio | VSD |
| MS_Project_2000_Fmt | 344 | Microsoft Project 2000 | MPP1 |
| MS_Outlook_Fmt | 345 | Microsoft Outlook | MSG, OFT |
| ELF_Relocatable_Fmt | 346 | ELF Relocatable | O |
| ELF_Executable_Fmt | 347 | ELF Executable |   |
| ELF_Dynamic_Lib_Fmt | 348 | ELF Dynamic Library | SO |
| MS_Word_XML_Fmt | 349 | Microsoft Word 2003 XML | XML1 |
| MS_Excel_XML_Fmt | 350 | Microsoft Excel 2003 XML | XML1 |
| MS_Visio_XML_Fmt | 351 | Microsoft Visio 2003 XML | VDX |
| SO_Text_XML_Fmt | 352 | StarOffice Text XML | SXW1, ODT1 |
| SO_Spreadsheet_XML_Fmt | 353 | StarOffice Spreadsheet XML | SXC1, ODS1 |
| SO_Presentation_XML_Fmt | 354 | StarOffice Presentation XML | SXI1, SXP1, ODP1 |
| XHTML_Fmt | 355 | XHTML | XML1 |
| MS_OutlookPST_Fmt | 356 | Microsoft Outlook PST | PST |
| RAR_Fmt | 357 | RAR | RAR |
| Lotus_Notes_NSF_Fmt | 358 | IBM Lotus Notes Database NSF/NTF | NSF |
| Macromedia_Flash_Fmt | 359 | SWF | SWF |
| MS_Word_2007_Fmt | 360 | Microsoft Word 2007 XML | DOCX, DOTX |
| MS_Excel_2007_Fmt | 361 | Microsoft Excel 2007 XML | XLSX, XLTX |
| MS_PPT_2007_Fmt | 362 | Microsoft PPT 2007 XML | PPTX, POTX, PPSX |
| OpenPGP_Fmt | 363 | OpenPGP Message Format (with new packet format) | PGP |
| Intergraph_V7_DGN_Fmt | 364 | Intergraph Standard FileFormat (ISFF) V7 DGN (non-OLE) | DGN1 |
| MicroStation_V8_DGN_Fmt | 365 | MicroStation V8 DGN (OLE) | DGN1 |
| MS_Word_Macro_2007_Fmt | 366 | Microsoft Word Macro 2007 XML | DOCM, DOTM |
| MS_Excel_Macro_2007_Fmt | 367 | Microsoft Excel Macro 2007 XML | XLSM, XLTM, XLAM |
| MS_PPT_Macro_2007_Fmt | 368 | Microsoft PPT Macro 2007 XML | PPTM, POTM, PPSM, PPAM |
| LZH_Fmt | 369 | LHA Archive | LZH, LHA |
| Office_2007_Fmt | 370 | Office 2007 document | XLSB |
| MS_XPS_Fmt | 371 | Microsoft XML Paper Specification (XPS) | XPS |
| Lotus_Domino_DXL_Fmt | 372 | IBM Lotus representation of Domino design elements in XML format | DXL |
| ODF_Text_Fmt | 373 | ODF Text | ODT1, SXW1, STW |
| ODF_Spreadsheet_Fmt | 374 | ODF Spreadsheet | ODS1, SXC1, STC |
| ODF_Presentation_Fmt | 375 | ODF Presentation | SXD1, SXI1, ODG1,, ODP1 |
| Legato_Extender_ONM_Fmt | 376 | Legato Extender Native Message ONM | ONM |
| bin_Unknown_Fmt | 377 | n/a |   |
| TNEF_Fmt | 378 | Transport Neutral Encapsulation Format (TNEF) | various |
| CADAM_Drawing_Fmt | 379 | CADAM Drawing | CDD |
| CADAM_Drawing_Overlay_Fmt | 380 | CADAM Drawing Overlay | CDO |
| NURSTOR_Drawing_Fmt | 381 | NURSTOR Drawing | NUR |
| HP_GLP_Fmt | 382 | HP Graphics Language (Plotter) | HPG |
| ASF_Fmt | 383 | Advanced Systems Format (ASF) | ASF |
| WMA_Fmt | 384 | Window Media Audio Format (WMA) | WMA |
| WMV_Fmt | 385 | Window Media Video Format (WMV) | WMV |
| EMX_Fmt | 386 | Legato EMailXtender Archives Format (EMX) | EMX |
| Z7Z_Fmt | 387 | 7 Zip Format(7z) | 7Z |
| MS_Excel_Binary_2007_Fmt | 388 | Microsoft Excel Binary 2007 | XLSB |
| CAB_Fmt | 389 | Microsoft Cabinet File (CAB) | CAB |
| CATIA_Fmt | 390 | CATIA Formats (CAT*) | CAT3 |
| YIM_Fmt | 391 | Yahoo Instant Messenger History | DAT1 |
| ODF_Drawing_Fmt | 392 | ODF Drawing | SXD1, SX1, ODG1 |
| Founder_CEB_Fmt | 393 | Founder Chinese E-paper Basic (ceb) | CEB |
| QPW_Fmt | 394 | Quattro Pro 9+ for Windows | QPW |
| MHT_Fmt | 395 | MHT format2 | MHT |
| MDI_Fmt | 396 | Microsoft Document Imaging Format | MDI |
| GRV_Fmt | 397 | Microsoft Office Groove Format | GRV |
| IWWP_Fmt | 398 | Apple iWork Pages format | PAGES, GZ1 |
| IWSS_Fmt | 399 | Apple iWork Numbers format | NUMBERS, GZ1 |
| IWPG_Fmt | 400 | Apple iWork Keynote format | KEY, GZ1 |
| BKF_Fmt | 401 | Windows Backup File | BKF |
| MS_Access_2007_Fmt | 402 | Microsoft Access 2007 | ACCDB |
| ENT_Fmt | 403 | Microsoft Entourage Database Format |   |
| DMG_Fmt | 404 | Mac Disk Copy Disk ImageFile |   |
| CWK_Fmt | 405 | AppleWorks File |   |
| OO3_Fmt | 406 | Omni Outliner File | OO3 |
| OPML_Fmt | 407 | Omni Outliner File | OPML |
| Omni_Graffle_XML_File | 408 | Omni Graffle XML File | GRAFFLE |
| PSD_Fmt | 409 | Photoshop Document | PSD |
| Apple_Binary_PList_Fmt | 410 | Apple Binary Property List format |   |
| Apple_iChat_Fmt | 411 | Apple iChat format |   |
| OOUTLINE_Fmt | 412 | OOutliner File | OOUTLINE |
| BZIP2_Fmt | 413 | Bzip 2 Compressed File | BZ2 |
| ISO_Fmt | 414 | ISO-9660 CD Disc Image Format | ISO |
| DocuWorks_Fmt | 415 | DocuWorks Format | XDW |
| RealMedia_Fmt | 416 | RealMedia Streaming Media | RM, RA |
| AC3Audio_Fmt | 417 | AC3 Audio File Format | AC3 |
| NEF_Fmt | 418 | Nero Encrypted File | NEF |
| SolidWorks_Fmt | 419 | SolidWorks Format Files | SLDASM, SLDPRT, SLDDRW |
| XFDL_Fmt | 420 | Extensible Forms Description Language | XFDL, XFD |
| Apple_XML_PList_Fmt | 421 | Apple XML Property List format |   |
| OneNote_Fmt | 422 | OneNote Note Format | ONE |
| Dicom_Fmt | 424 | Digital Imaging and Communications in Medicine | DCM |
| EnCase_Fmt | 425 | Expert Witness Compression Format (EnCase) | E01, L01, Lx01 |
| Scrap_Fmt | 426 | Shell Scrap Object File | SHS |
| MS_Project_2007_Fmt | 427 | Microsoft Project 2007 | MPP1 |
| MS_Publisher_98_Fmt | 428 | Microsoft Publisher 98/2000/2002/2003/2007/ | PUB1 |
| Skype_Fmt | 429 | Skype Log File | DBB |
| Hl7_Fmt | 430 | Health level7 message | HL7 |
| MS_OutlookOST_Fmt | 431 | Microsoft Outlook OST | OST |
| Epub_Fmt | 432 | Electronic Publication | EPUB |
| MS_OEDBX_Fmt | 433 | Microsoft Outlook Express DBX | DBX |
| BB_Activ_Fmt | 434 | BlackBerry Activation File | DAT1 |
| DiskImage_Fmt | 435 | Disk Image |   |
| Milestone_Fmt | 436 | Milestone Document | MLS, ML3, ML4, ML5, ML6, ML7, ML8, ML9 |
| E_Transcript_Fmt | 437 | RealLegal E-Transcript File | PTX |
| PostScript_Font_Fmt | 438 | PostScript Type 1 Font | PFB |
| Ghost_DiskImage_Fmt | 439 | Ghost Disk Image File | GHO, GHS |
| JPEG_2000_JP2_File_Fmt | 440 | JPEG-2000 JP2 File Format Syntax (ISO/IEC 15444-1) | JP2, JPF, J2K, JPWL, JPX, PGX |
| Unicode_HTML_Fmt | 441 | Unicode HTML | HTM1, HTML1 |
| CHM_Fmt | 442 | Microsoft Compiled HTML Help | CHM |
| EMCMF_Fmt | 443 | Documentum EMCMF format | EMCMF |
| MS_Access_2007_Tmpl_Fmt | 444 | Microsoft Access 2007 Template | ACCDT |
| Jungum_Fmt | 445 | Samsung Electronics Jungum Global document | GUL |
| JBIG2_Fmt | 446 | JBIG2 File Format | JB2, JBIG2 |
| EFax_Fmt | 447 | eFax file | EFX |
| AD1_Fmt | 448 | AD1 Evidence file | AD1 |
| SketchUp_Fmt | 449 | Google SketchUp | SKP |
| GWFS_Email_Fmt | 450 | Group Wise File Surf email | GWFS |
| JNT_Fmt | 451 | Windows Journal format | JNT |
| Yahoo_yChat_Fmt | 452 | Yahoo! Messenger chat log | YCHAT |
| PaperPort_MAX_File_Fmt | 453 | PaperPort image file | MAX |
| ARJ_Fmt | 454 | ARJ (Archive by Robert Jung) file format | ARJ |
| RPMSG_Fmt | 455 | Microsoft Outlook Restricted Permission Message | RPMSG |
| MAT_Fmt | 456 | MATLAB file format | MAT, FIG |
| SGY_Fmt | 457 | SEG-Y Seismic Data format | SGY, SEGY |
| CDXA_MPEG_PS_Fmt | 458 | MPEG-PS container with CDXA stream | MPG1 |
| EVT_Fmt | 459 | Microsoft Windows NT Event Log | EVT |
| EVTX_Fmt | 460 | Microsoft Windows Vista Event Log | EVTX |
| MS_OutlookOLM_Fmt | 461 | Microsoft Outlook for Macintosh format | OLM |
| WARC_Fmt | 462 | Web ARChive | WARC |
| JAVACLASS_Fmt | 463 | Java Class format | CLASS |
| VCF_Fmt | 464 | Microsoft Outlook vCard fileformat | VCF |
| EDB_Fmt | 465 | Microsoft Exchange Server Database file format | EDB |
| ICS_Fmt | 466 | Microsoft Outlook iCalendarfile format | ICS, VCS |
| MS_Visio_2013_Fmt | 467 | Microsoft Visio 2013 | VSDX, VSTX, VSSX |
| MS_Visio_2013_Macro_Fmt | 468 | Microsoft Visio 2013 macro | VSDM, VSTM, VSSM |
| ICHITARO_Compr_Fmt | 469 | ICHITARO Compressed format | JTDC |
| IWWP13_Fmt | 470 | Apple iWork 2013 Pages format | IWA  |
| IWSS13_Fmt | 471 | Apple iWork 2013 Numbers format | IWA |
| IWPG13_Fmt | 472 | Apple iWork 2013 Keynote format | IWA |
| XZ_Fmt | 473 | XZ archive format | XZ |
| Sony_WAVE64_Fmt | 474 | Sony Wave64 format | W64 |
| Conifer_WAVPACK_Fmt | 475 | Conifer Wavpack format | WV |
| Xiph_OGG_VORBIS_Fmt | 476 | Xiph Ogg Vorbis format | OGG |
| MS_Visio_2013_Stencil_Fmt | 477 | MS Visio 2013 stencil format | VSSX |
| MS_Visio_2013_Stencil_Macro_Fmt | 478 | MS Visio 2013 stencil Macro format | VSSM |
| MS_Visio_2013_Template_Fmt | 479 | MS Visio 2013 template format | VSTX |
| MS_Visio_2013_Template_Macro_Fmt | 480 | MS Visio 2013 template Macro format | VSTM |
| Borland_Reflex_2_Fmt | 481 | Borland Reflex 2 format | R2D |
| PKCS_12_Fmt | 482 | PKCS #12 (p12) format | P12, PFX |
| B1_Fmt | 483 | B1 format | B1 |
| ISO_IEC_MPEG_4_Fmt | 484 | ISO/IEC MPEG-4 format | MP4 |

