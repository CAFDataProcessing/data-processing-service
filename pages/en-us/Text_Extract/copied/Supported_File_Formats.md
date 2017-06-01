---
layout: default
title: Document Class & Format Code Reference
---

# Supported Formats
The tables in this section provide the following information:

- The file formats supported by the Filter API and File Extraction API of the Text Extract Worker. The
supported versions and the format’s extension are also listed.
- The document reader used to filter each format.

### Key to Support Tables

<table>
  <tr>
    <th>Symbol</th>
    <th>Description</th>
  </tr>
  <tr>
    <td align="center">Y</td>
    <td>Format is supported<br>Metadata can be extracted for this format</td>
  </tr>
  <tr>
    <td align="center">N</td>
    <td>Format is not supported<br>Metadata cannot be extracted for this format</td>
  </tr>
  <tr>
    <td align="center">P</td>
    <td>Partial metadata is extracted from this format. Some non-standard fields are not extracted</td>
  </tr>
</table>

## Archive Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>7-Zip</td>
    <td>4.57</td>
    <td>multiarcsr</td>
    <td>7Z</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>AD1</td>
    <td>n/a</td>
    <td>ad1sr</td>
    <td>AD1</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>ARJ</td>
    <td>n/a</td>
    <td>multiarcsr</td>
    <td>ARJ</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>B1</td>
    <td>n/a</td>
    <td>b1sr</td>
    <td>B1</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>BinHex</td>
    <td>n/a</td>
    <td>kvhqxsr</td>
    <td>HQX</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Bzip2</td>
    <td>n/a</td>
    <td>bzip2sr</td>
    <td>BZ2</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td rowspan="2">Expert Witness<br>Compression Format<br>(EnCase)</td>
    <td>6</td>
    <td>encasesr</td>
    <td>E01, L01</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>7</td>
    <td>encase2sr</td>
    <td>Lx01</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>GZIP</td>
    <td>2</td>
    <td>kvgzsr</td>
    <td>GZ</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>ISO</td>
    <td>n/a</td>
    <td>isosr</td>
    <td>ISO</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Java Archive</td>
    <td>n/a</td>
    <td>unzip</td>
    <td>JAR</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Legato EmailXtender<br>Archive</td>
    <td>n/a</td>
    <td>emxsr</td>
    <td>EMX</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>MacBinary</td>
    <td>n/a</td>
    <td>macbinsr</td>
    <td>BIN</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Mac Disk Copy Disk Image</td>
    <td>n/a</td>
    <td>dmgs</td>
    <td>DMG</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Backup File</td>
    <td>n/a</td>
    <td>bkfsr</td>
    <td>BKF</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Cabinet Format</td>
    <td>1.3</td>
    <td>cabsr</td>
    <td>CAB</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Complied HTML<br>Help</td>
    <td>3</td>
    <td>chmsr</td>
    <td>CHM</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Compressed<br>Folder</td>
    <td>n/a</td>
    <td>lzhsr</td>
    <td>LZH<br>LHA</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>PKZIP</td>
    <td>through<br>9.0</td>
    <td>unzip</td>
    <td>ZIP</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>RAR Archive</td>
    <td>2.0<br>through<br>3.5</td>
    <td>rarsr</td>
    <td>RAR</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Tape Archive</td>
    <td>n/a</td>
    <td>tarsr</td>
    <td>TAR</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>UNIX Compress</td>
    <td>n/a</td>
    <td>kvzeesr</td>
    <td>Z</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>UUEncoding</td>
    <td>all<br>versions</td>
    <td>uudsr</td>
    <td>UUE</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>XZ</td>
    <td>n/a</td>
    <td>multiarcsr</td>
    <td>XZ</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Windows Scrap File</td>
    <td>n/a</td>
    <td>olesr</td>
    <td>SHS</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>WinZIP</td>
    <td>through<br>10</td>
    <td>unzip</td>
    <td>ZIP</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
</table>

## Binary Format

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>Executible</td>
    <td>n/a</td>
    <td>exesr</td>
    <td>EXE</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Link Library</td>
    <td>n/a</td>
    <td>exesr</td>
    <td>DLL</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
</table>

## Computer-Aided Design Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>AutoCAD<br>Drawing</td>
    <td>R13, R14,<br>R15/2000, 2004,<br>2007, 2010, 2013</td>
    <td>kpDWGrdr</td>
    <td>DWG</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>AutoCAD<br>Drawing<br>Exchange</td>
    <td>R13, R14,<br>R15/2000, 2004,<br>2007, 2010, 2013</td>
    <td>kpDXFrdr</td>
    <td>DXF</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>CATIA Formats</td>
    <td>5</td>
    <td>kpCATrdr</td>
    <td>CAT</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td rowspan="3">Microsoft Visio</td>
    <td rowspan="2">4, 5, 2000, 2002,<br>2003, 2007, 2010</td>
    <td>vsdsr</td>
    <td>VSD</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
	<td rowspan="2">No VSD2 reader in formats.ini file. During testing found that for all VSD, VSS and VST files subfiles are extracted but I get a KV error "No Reader" on an embedded word document. In the documentation there is a caveat stating that extraction of OLE objects is supported only on Windows but we should not get a KV error in any case.</td>
  </tr>
    <tr>
    <td>kpVSD2rdr</td>
    <td>VSD<br>VSS<br>VST</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>2013</td>
    <td>kpVSDXrdr</td>
    <td>VSDM<br>VSSM<br>VSTM<br>VSDX<br>VSSX<br>VSTX</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
	<td>No VSDX reader in formats.ini file. During testing found that for all VSDM, VSSM, VSTM, VSDX, VSSX, VSTX files get content and metadata returned. However if these files have an embedded word document there are no subfiles extracted.</td>
  </tr>
</table>
[CAF-1108 has been logged to investigate the issues with Visio](https://jira.autonomy.com/browse/CAF-1108)

## Database Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>dBase<br>Drawing</td>
    <td>III+, IV</td>
    <td>dbfsr</td>
    <td>DBF</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft<br>Access</td>
    <td>95, 97, 2000, 2002, 2003,<br>2007, 2010, 2013</td>
    <td>mdbsr</td>
    <td>MDB,<br>ACCDB</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft<br>Project</td>
    <td>2000, 2002, 2003, 2007,<br>2010,2013</td>
    <td>mppsr</td>
    <td>MPP</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
</table>

## Desktop Publisher

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>Microsoft Publisher</td>
    <td>98 to 2013</td>
    <td>mspubsr</td>
    <td>PUB</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
</table>

## Display Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>Adobe PDF</td>
    <td>1.1 to 1.7</td>
    <td>pdfsr</td>
    <td>PDF</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
</table>

## Graphic Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>Computer Graphics<br>Metafile</td>
    <td>n/a</td>
    <td>kpcgmrdr</td>
    <td>CGM</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>CorelDRAW</td>
    <td>through<br>9.0, 10, 11,<br>12, X3</td>
    <td>kpcdrrdr</td>
    <td>CDR</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>DCX Fax System</td>
    <td>n/a</td>
    <td>kpdcxrdr</td>
    <td>DCX</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Digital Imaging &amp;<br>Communication in<br>Medicine (DICOM)</td>
    <td>n/a</td>
    <td>dcmsr</td>
    <td>DCM</td>
    <td>N</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Encapsulated PostScript<br>(raster)</td>
    <td>TIFF<br>header</td>
    <td>kpepsrdr</td>
    <td>EPS</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Enhanced Metafile</td>
    <td>n/a</td>
    <td>kpemfrdr</td>
    <td>EMF</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>GIF</td>
    <td>87, 89</td>
    <td>gifsr</td>
    <td>GIF</td>
    <td>N</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>JBIG2</td>
    <td>n/a</td>
    <td>kpJBIG2rdr</td>
    <td>JBIG2</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>JPEG</td>
    <td>n/a</td>
    <td>jpgsr</td>
    <td>JPEG</td>
    <td>N</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td rowspan="2">JPEG 2000</td>
    <td rowspan="2">n/a</td>
    <td rowspan="2">kpjp2000rdr,<br>jp2000sr</td>
    <td rowspan="2">JP2, JPF,<br>J2K, JPWL,<br>JPX, PGX</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>N</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Lotus AMIDraw<br>Graphics</td>
    <td>n/a</td>
    <td>kpsdwrdr</td>
    <td>SDW</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Lotus Pic</td>
    <td>n/a</td>
    <td>kppicrdr</td>
    <td>PIC</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Macintosh Raster</td>
    <td>2</td>
    <td>kppctrdr</td>
    <td>PIC<br>PCT</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>MacPaint</td>
    <td>n/a</td>
    <td>kpmacrdr</td>
    <td>PNTG</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Office Drawing</td>
    <td>n/a</td>
    <td>kpmsordr</td>
    <td>MSO</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Omni Graffle</td>
    <td>n/a</td>
    <td>kpGFLrdr</td>
    <td>GRAFFLE</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>PC Paintbrush</td>
    <td>3</td>
    <td>kppcxrdr</td>
    <td>PCX</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Portable Network<br>Graphics</td>
    <td>n/a</td>
    <td>pngsr</td>
    <td>PNG</td>
    <td>N</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>SGI RGB Image</td>
    <td>n/a</td>
    <td>kpsgirdr</td>
    <td>RGB</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Sun Raster Image</td>
    <td>n/a</td>
    <td>kpsunrdr</td>
    <td>RS</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Tagged Image File</td>
    <td>through<br>6.0</td>
    <td>tifsr</td>
    <td>TIFF</td>
    <td>N</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Truevision Targa</td>
    <td>2</td>
    <td>kptrardr</td>
    <td>TGA</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Windows Animated<br>Cursor</td>
    <td>n/a</td>
    <td>kpanirdr</td>
    <td>ANI</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Windows Bitmap</td>
    <td>n/a</td>
    <td>bmpsr</td>
    <td>BMP</td>
    <td>N</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Windows Icon Cursor</td>
    <td>n/a</td>
    <td>kpicordr</td>
    <td>ICO</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Windows Metafile</td>
    <td>3</td>
    <td>kpwmfrdr</td>
    <td>WMF</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>WordPerfect Graphics 1</td>
    <td>1</td>
    <td>kpwpgrdr</td>
    <td>WPG</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>WordPerfect Graphics 2</td>
    <td>2, 7</td>
    <td>kpwg2rdr</td>
    <td>WPG</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
</table>

## Mail Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
	<th>Notes</th>
  </tr>
  <tr>
    <td>Documentation<br>EMCMF</td>
    <td>n/a</td>
    <td>msgs</td>
    <td>EMCMF</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Domino XML<br>Language</td>
    <td>n/a</td>
    <td>dxlsr</td>
    <td>DXL</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>GroupWise FileSurf</td>
    <td>n/a</td>
    <td>gwfssr</td>
    <td>GWFS</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Legato Extender</td>
    <td>n/a</td>
    <td>onmsr</td>
    <td>ONM</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Lotus Notes<br>database</td>
    <td>4, 5, 6.0, 6.5, 7.0,<br>8.0</td>
    <td>nsfsr</td>
    <td>NSF</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Mailbox</td>
    <td>Thunderbird 1.0,<br>Eudora 6.2</td>
    <td>mbxsr</td>
    <td>MBX</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Entourage<br>Database</td>
    <td>2004</td>
    <td>entsr</td>
    <td>various</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Outlook</td>
    <td>97, 2000, 2002,<br>2003, 2007, 2010,<br>2013</td>
    <td>msgsr</td>
    <td>MSG,<br>OFT</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Outlook<br>DBX</td>
    <td>5.0, 6.0</td>
    <td>dbxsr</td>
    <td>DBX</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Outlook<br>Express</td>
    <td>Windows 6,<br>MacIntosh 5</td>
    <td>mbxsr</td>
    <td>EML</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Outlook<br>iCalendar</td>
    <td>1.0, 2.0</td>
    <td>icssr</td>
    <td>ICS,<br>VCS</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Outlook for<br>MacIntosh</td>
    <td>2011</td>
    <td>olmsr</td>
    <td>OLM</td>
    <td>N</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Outlook<br>Offline Storage File</td>
    <td>97, 2000, 2002,<br>2003, 2007, 2010,<br>2013</td>
    <td>pffsr</td>
    <td>OST</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Outlook<br>Personal Folder</td>
    <td>97, 2000, 2002,<br>2003, 2007, 2010,<br>2013</td>
    <td>pstnsr</td>
    <td>PST</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
	<td>Password protected PST files that use High Encryption (Microsoft Outlook 2003 only) are not supported</td>
  </tr>
  <tr>
    <td>Microsoft Outlook<br>vCard Contact</td>
    <td>2.1, 3.0, 4.0</td>
    <td>vcfsr</td>
    <td>VCF</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Text Mail (MIME)</td>
    <td>n/a</td>
    <td>mbxsr</td>
    <td>various</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Transport Neutral<br>Encapsulation<br>Format</td>
    <td>n/a</td>
    <td>tnefsr</td>
    <td>various</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
</table>

## Multimedia Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>Advanced Systems Format</td>
    <td>1.2</td>
    <td>asfsr</td>
    <td>ASF<br>WMA<br>WMV</td>
    <td>N</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Audio Interchange File<br>Format</td>
    <td>n/a</td>
    <td>aiffsr</td>
    <td>AIFF</td>
    <td>M</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Wave Sound</td>
    <td>n/a</td>
    <td>riffsr</td>
    <td>WAV</td>
    <td>M</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>MPEG-1 Audio layer 3</td>
    <td>ID3 v1 and<br>v2</td>
    <td>mp3sr</td>
    <td>MP3</td>
    <td>M</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>MPEG-2 Audio</td>
    <td>n/a</td>
    <td>mp3sr</td>
    <td>MPEGA</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>MPEG-4 Audio</td>
    <td>n/a</td>
    <td>mpeg4sr</td>
    <td>MP4<br>3GP</td>
    <td>M</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>NeXT/Sun Audio</td>
    <td>n/a</td>
    <td>sunadsr</td>
    <td>AU</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>QuickTime Movie</td>
    <td>2, 3, 4</td>
    <td>mpeg4sr</td>
    <td>QT<br>MOV</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
</table>

## Presentation Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>Apple iWork Keynote</td>
    <td>2, 3, '08, '09</td>
    <td>kpIWPGrdr</td>
    <td>GZ</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Applix Presents</td>
    <td>4.0, 4.2, 4.3,<br>4.4</td>
    <td>kpagrdr</td>
    <td>AG</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Corel Presentations</td>
    <td>6, 7, 8, 9, 10,<br>11, 12, X3</td>
    <td>kpshwrdr</td>
    <td>SHW</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Extensible Forms<br>Description Language</td>
    <td>n/a</td>
    <td>kpXFDLrdr</td>
    <td>XFD<br>XFDL</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Lotus Freelance<br>Graphics</td>
    <td>96, 97, 98,<br>R9, 9.8</td>
    <td>kpprzrdr</td>
    <td>PRZ</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Lotus Freelance<br>Graphics 2</td>
    <td>2</td>
    <td>kpprerdr</td>
    <td>PRE</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Macromedia Flash</td>
    <td>through 8.0</td>
    <td>swfsr</td>
    <td>SWF</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft OneNote</td>
    <td>2007, 2010,<br>2013</td>
    <td>kpONErdr</td>
    <td>ONE<br>ONETOC2</td>
    <td>Y</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft PowerPoint<br>PC</td>
    <td>4</td>
    <td>ppcsr</td>
    <td>PPT</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Microsoft PowerPoint<br>Windows</td>
    <td>95</td>
    <td>pptsr</td>
    <td>PPT</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Microsoft PowerPoint<br>Windows</td>
    <td>97, 2000,<br>2002, 2003</td>
    <td>pptsr</td>
    <td>PPT<br>PPS<br>POT</td>
    <td>Y</td>
    <td>Y</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Microsoft PowerPoint<br>Windows XML</td>
    <td>2007, 2010,<br>2013</td>
    <td>ppxsr</td>
    <td>PPTX<br>PPTM<br>POTX<br>POTM<br>PPSX<br>PPSM<br>PPAM</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>OASIS Open<br>Document Format</td>
    <td>1, 2</td>
    <td>kpodfrdr</td>
    <td>SXD<br>SXI<br>ODG<br>ODP</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>OpenOffice Impress</td>
    <td>1, 1.1</td>
    <td>sosr</td>
    <td>SXI<br>SXP<br>ODP</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>StarOffice Impress</td>
    <td>6, 7</td>
    <td>sosr</td>
    <td>SXI<br>SXP<br>ODP</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
</table>

## Spreadsheet Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>Apple iWork Numbers</td>
    <td>'08, '09</td>
    <td>iwsssr</td>
    <td>GZ</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Applix Spreadsheets</td>
    <td>4.2, 4.3, 4.4</td>
    <td>assr</td>
    <td>AS</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Comma Separated<br>Values</td>
    <td>n/a</td>
    <td>csvsr</td>
    <td>CSV</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td rowspan="2">Corel Quattro Pro</td>
    <td>5, 6, 7, 8</td>
    <td>qpssr</td>
    <td>WB2<br>WB3</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>X4</td>
    <td>qpwsr</td>
    <td>QPW</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Data Interchange Format</td>
    <td>n/a</td>
    <td>difsr</td>
    <td>DIF</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Lotus 1-2-3</td>
    <td>96, 97, R9, 9.8</td>
    <td>l123sr</td>
    <td>123</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Lotus 1-2-3</td>
    <td>2, 3, 4, 5</td>
    <td>wkssr</td>
    <td>WK4</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Lotus 1-2-3 Charts</td>
    <td>2, 3, 4, 5</td>
    <td>kpchtrdr</td>
    <td>123</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Excel Charts</td>
    <td>2, 3, 4, 5, 6, 7</td>
    <td>kpchtrdr</td>
    <td>XLS</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Excel<br>MacIntosh</td>
    <td>98, 2001, v.X,<br>2004</td>
    <td>xlssr</td>
    <td>XLS</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Excel<br>Windows</td>
    <td>2.2 through<br>2003</td>
    <td>xlssr</td>
    <td>XLS<br>XLW<br>XLT<br>XLA</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Excel Windows<br>XML</td>
    <td>2007, 2010,<br>2013</td>
    <td>xlsxsr</td>
    <td>XLSX<br>XLTX<br>XLSM<br>XLTM<br>XLAM</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Excel Binary<br>Format</td>
    <td>2007, 2010,<br>2013</td>
    <td>xlsbsr</td>
    <td>XLSB</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Works<br>Spreadsheet</td>
    <td>2, 3, 4</td>
    <td>mwssr</td>
    <td>S30<br>S40</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>OASIS Open Document<br>Format</td>
    <td>1, 2</td>
    <td>odfsssr</td>
    <td>ODS<br>SXC<br>STC</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>OpenOffice Calc</td>
    <td>1, 1.1</td>
    <td>sosr</td>
    <td>SXC<br>ODS<br>OTS</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>StarOffice Calc</td>
    <td>6, 7</td>
    <td>sosr</td>
    <td>SXC<br>ODS</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
</table>

## Text and Markup Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>ANSI</td>
    <td>n/a</td>
    <td>afsr</td>
    <td>TXT</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>ASCII</td>
    <td>n/a</td>
    <td>afsr</td>
    <td>TXT</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>HTML</td>
    <td>3, 4</td>
    <td>htmsr</td>
    <td>HTM</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Microsoft Excel Windows<br>XML</td>
    <td>2003</td>
    <td>xmlsr</td>
    <td>XML</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Word Windows<br>XML</td>
    <td>2003</td>
    <td>xmlsr</td>
    <td>XML</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Visio XML</td>
    <td>2003</td>
    <td>xmlsr</td>
    <td>VDX<br>VTX</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>MIME HTML</td>
    <td>n/a</td>
    <td>mhtsr</td>
    <td>MHT</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Rich Text Format</td>
    <td>1 through<br>1.7</td>
    <td>rtfsr</td>
    <td>RTF</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Unicode HTML</td>
    <td>n/a</td>
    <td>unihtmsr</td>
    <td>HTM</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Unicode Text</td>
    <td>3, 4</td>
    <td>unisr</td>
    <td>TXT</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>XHTML</td>
    <td>1.0</td>
    <td>htmsr</td>
    <td>HTM</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>XL (generic)</td>
    <td>1.0</td>
    <td>xmlsr</td>
    <td>XML</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
</table>

## Word Processing Formats

<table>
  <tr>
    <th>Format</th>
    <th>Version</th>
    <th>Reader</th>
    <th>Extension</th>
    <th>Filter</th>
    <th>Extract</th>
    <th>Metadata</th>
  </tr>
  <tr>
    <td>Adobe FrameMaker<br>Interchange Format</td>
    <td>5, 5.5, 6, 7</td>
    <td>mifsr</td>
    <td>MIF</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Apple iChat Log</td>
    <td>1, AV 2,<br>AV 2.1, AV 3</td>
    <td>ichatsr</td>
    <td>ICHAT</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td rowspan="2">Apple iWork Pages</td>
    <td>'08, '09</td>
    <td>iwwpsr</td>
    <td>GZ</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>'13</td>
    <td>iwwp13sr</td>
    <td>PAGES</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Applix Words</td>
    <td>3.11, 4, 4.1,<br>4.2, 4.3, 4.4</td>
    <td>awsr</td>
    <td>AW</td>
    <td>Y</td>
    <td>Y</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Corel WordPerfect<br>Linux</td>
    <td>6.0, 8.1</td>
    <td>wp6sr</td>
    <td>WPS</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Corel WordPerfect<br>MacIntosh</td>
    <td>1.02, 2, 2.1,<br>2.2, 3, 3.1</td>
    <td>wpmsr</td>
    <td>WPM</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Corel WordPerfect<br>Windows</td>
    <td>5, 5.1</td>
    <td>wosr</td>
    <td>WO</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Corel WordPerfect<br>Windows</td>
    <td>6, 7, 8, 9, 10,<br>11, 12, X3</td>
    <td>wp6sr</td>
    <td>WPD</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>DisplayWrite</td>
    <td>4</td>
    <td>dw4sr</td>
    <td>IP</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Folio Flat Lite</td>
    <td>3.1</td>
    <td>foliosr</td>
    <td>FFF</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Founder Chinese<br>E-paper Basic</td>
    <td>3.2.1</td>
    <td>cebsr</td>
    <td>CEB</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Fujitsu Oasys</td>
    <td>7</td>
    <td>oa2sr</td>
    <td>OA2</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td rowspan="2">Haansoft Hangul</td>
    <td>97</td>
    <td>hwpsr</td>
    <td>HWP</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>2002, 2005,<br>2007, 2010</td>
    <td>hwposr</td>
    <td>HWP</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Health level7</td>
    <td>2.0</td>
    <td>hl7sr</td>
    <td>HL7</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>IBM DCA/RFT<br>(Revisable Form Text)</td>
    <td>SC23-0758-1</td>
    <td>dcasr</td>
    <td>DC</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>JustSystems Ichitaro</td>
    <td>8 through 2013</td>
    <td>jtdsr</td>
    <td>JTD</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Lotus AMI Pro</td>
    <td>2, 3</td>
    <td>lasr</td>
    <td>SAM</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Lotus AMI Profressional<br>Write Plus</td>
    <td>2.1</td>
    <td>lasr</td>
    <td>AMI</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Lotus Word Pro</td>
    <td>96, 97, R9</td>
    <td>lwpsr</td>
    <td>LWP</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>Lotus SmartMaster</td>
    <td>96, 97</td>
    <td>lwpsr</td>
    <td>MWP</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td rowspan="2">Microsoft Word<br>MacIntosh</td>
    <td>4, 5, 6, 98</td>
    <td>mbsr</td>
    <td>DOC</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>2001, v.X,<br>2004</td>
    <td>mw8sr</td>
    <td>DOC<br>DOT</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Word PC</td>
    <td>4, 5, 5.5, 6</td>
    <td>mwsr</td>
    <td>DOC</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Word<br>Windows</td>
    <td>1.0, 2.0</td>
    <td>misr</td>
    <td>DOC</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Word<br>Windows</td>
    <td>6, 7, 8, 95</td>
    <td>mw6sr</td>
    <td>DOC</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Word<br>Windows</td>
    <td>97, 2000,<br>2002, 2003</td>
    <td>mw8sr</td>
    <td>DOC<br>DOT</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Word<br>Windows XML</td>
    <td>2007, 2010,<br>2013</td>
    <td>mwxsr</td>
    <td>DOCM<br>DOCX<br>DOTX<br>DOTM</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Microsoft Works</td>
    <td>1, 2, 3, 4</td>
    <td>mswsr</td>
    <td>WPS</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Works</td>
    <td>6, 2000</td>
    <td>msw6sr</td>
    <td>WPS</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Microsoft Windows<br>Write</td>
    <td>1, 2, 3</td>
    <td>mwsr</td>
    <td>WRI</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>OASIS Open<br>Document Format</td>
    <td>1, 2</td>
    <td>odfwpsr</td>
    <td>ODT<br>SXW<br>STW</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Omni Outliner</td>
    <td>v3, OPML,<br>OOutline</td>
    <td>oo3sr</td>
    <td>OO3<br>OPML<br>OOUTLINE</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>OpenOffice Writer</td>
    <td>1, 1.1</td>
    <td>sosr</td>
    <td>SXW<br>ODT</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Open Publication<br>Structure eBook</td>
    <td>2.0, 3.0</td>
    <td>epubsr</td>
    <td>EPUB</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>StarOffice Writer</td>
    <td>6, 7</td>
    <td>sosr</td>
    <td>SXW<br>ODT</td>
    <td>Y</td>
    <td>N</td>
    <td>Y</td>
  </tr>
  <tr>
    <td>Skype Log</td>
    <td>3</td>
    <td>skypesr</td>
    <td>DBB</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>WordPad</td>
    <td>through 2003</td>
    <td>rtfsr</td>
    <td>RTF</td>
    <td>Y</td>
    <td>N</td>
    <td>P</td>
  </tr>
  <tr>
    <td>XML Paper<br>Specification</td>
    <td>n/a</td>
    <td>xpssr</td>
    <td>XPS</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>XyWrite</td>
    <td>4.12</td>
    <td>xywsr</td>
    <td>XY4</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
  <tr>
    <td>Yahoo! Instant<br>Messenger</td>
    <td>n/a</td>
    <td>yimsr</td>
    <td>DAT</td>
    <td>Y</td>
    <td>N</td>
    <td>N</td>
  </tr>
</table>
