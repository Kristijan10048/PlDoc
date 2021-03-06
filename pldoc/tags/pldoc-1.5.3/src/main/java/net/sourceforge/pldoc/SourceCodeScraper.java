package net.sourceforge.pldoc;

import java.io.*;


/**
 * Siphon off the source code in the Reader and write out as XML to a Writer. 
 *
 *<p>This makes database source code available for subsequent display or processing.
 *<pre>
 *<code>
 *<xml ...>
 *<file>
 *<line number="1">line 1 text</line>
 *<line number="2">line 2 text</line>
 *<line number="line-number">line text</line>
 *</file>
 %</xml>
 *</code>
 *</pre>
 *</p>
 *
 * Cribbed fron {@link apache.commons.io.input.TeeInputSteam} 
 */
public class SourceCodeScraper extends FilterReader
{

  /** Additional destination for Read contents.
   */
  private PrintWriter writer;

  /** Close sink when this Reader closes.
  */
  private boolean autoclose;  

  /* Clone the content of the Reader into the Writer as XML, optionally adding an XSLT stylesheet reference.
   * 
   * @param reader the source code to be cloned
   * @param writer the source code to be cloned
   * @param autoclose Should the Writer be closed when the Reader closes?
   * @param xsltHref Optional location of an XSLT stylesheet to view the source code.
   */

  public SourceCodeScraper (LineNumberReader reader, PrintWriter writer, boolean autoclose, String xsltHref)
  throws IOException 
  {
    super(reader);
    this.writer = writer;
    this.autoclose = autoclose; 
    this.writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    if ( null != xsltHref )
    {
      this.writer.println(String.format("<?xml-stylesheet type=\"text/xsl\" href=\"%s\" ?>", xsltHref ) );
    }
    this.writer.println("<file>");
  }

  /* Clone the content of the Reader into the Writer as XML, optionally adding an XSLT stylesheet reference.
   * 
   * @param reader the source code to be cloned
   * @param writer the source code to be cloned
   * @param autoclose Should the Writer be closed when the Reader closes?
   * @param xsltHref Optional location of an XSLT stylesheet to view the source code.
   */
  public SourceCodeScraper (Reader reader, Writer writer, boolean autoclose, String xsltHref)
  throws IOException 
  {
    this(new LineNumberReader(reader), new PrintWriter(writer), autoclose, xsltHref );
  }

  /* Clone the content of the Reader into the Writer as XML, optionally adding an XSLT stylesheet reference.
   * 
   * @param reader the source code to be cloned
   * @param writer the source code to be cloned
   * @param autoclose Should the Writer be closed when the Reader closes?
   * @param xsltFile Optional location of an XSLT stylesheet to view the source code.
   */
  public SourceCodeScraper (LineNumberReader reader, PrintWriter writer, boolean autoclose, File xsltFile)
  throws IOException 
  {
    this(reader, writer, autoclose, (null == xsltFile ) ? null : xsltFile.getCanonicalPath() );
  }


  /* Clone the content of the Reader into the Writer as XML, optionally adding an XSLT stylesheet reference.
   * 
   * @param reader the source code to be cloned
   * @param writer the source code to be cloned
   * @param autoclose Should the Writer be closed when the Reader closes?
   * @param xsltFile Optional location of an XSLT stylesheet to view the source code.
   */
  public SourceCodeScraper (Reader reader, Writer writer, boolean autoclose, File xsltFile)
  throws IOException 
  {
    this(new LineNumberReader(reader), new PrintWriter(writer), autoclose, xsltFile);
  }

  /* Clone the content of the Reader into the Writer as XML, optionally adding an XSLT stylesheet reference.
   * 
   * @param reader the source code to be cloned
   * @param writer the source code to be cloned
   * @param xsltFile Optional location of an XSLT stylesheet to view the source code.
   */
  public SourceCodeScraper (LineNumberReader reader, PrintWriter writer, File xsltFile)
  throws IOException 
  {
    this(reader, writer,false, xsltFile);
  }

  /* Clone the content of the Reader into the Writer as XML, optionally adding an XSLT stylesheet reference.
   * 
   * @param reader the source code to be cloned
   * @param writer the source code to be cloned
   * @param xsltFile Optional location of an XSLT stylesheet to view the source code.
   */
  public SourceCodeScraper (Reader reader, Writer writer, File xsltFile)
  throws IOException 
  {
    this(new LineNumberReader(reader), new PrintWriter(writer),false, xsltFile);
  }


  public void close()
  throws IOException 
  {
    super.close();
    writer.println("]]></line>\n</file>");
    if (autoclose)
    {
	writer.flush();
    	writer.close(); 
    }
  }

  public int read()
  throws IOException 
  {
    int c = super.read();

    if ( -1 == c )
    {
      if (autoclose)
      {
	writer.println("]]></line>\n</file>");
        writer.close();
      }
    }
    else
    {
      if ( '\n' == c )
      {
	writer.print("]]></line>");
      }
      writer.write(c);
      if ( '\n' == c )
      {
	writer.print(String.format("<line number=\"%d\"><![CDATA["
				   ,((LineNumberReader) in).getLineNumber()
				   )
		     );
      }
    }


    return c;
  }

  public int read(char[] cbuf)
  throws IOException 
  {
    int lineNumber =  ((LineNumberReader) in).getLineNumber();
    int charactersRead = super.read(cbuf);

    if ( -1 == charactersRead )
    {
      if (autoclose)
      {
	writer.println("]]></line>\n</file>");
        writer.flush();
        writer.close();
      }
    }
    else
    {
      StringBuilder stringBuilder = new StringBuilder();
      for (int offset = 0 ; offset < charactersRead ;  offset++)
      {
       if ('\n' == cbuf[offset] )
       {
	 if ( lineNumber > 0 ) 
	 { 
	   stringBuilder.append("]]></line>");
	 }
	 lineNumber++;

	 stringBuilder.append(cbuf[offset]);

	 stringBuilder.append(String.format("<line number=\"%d\"><![CDATA["
			       ,lineNumber
			       )
		       );
       }
       else
       {
	 stringBuilder.append(cbuf[offset]);
       }
      }
      writer.write(stringBuilder.toString());
    }

    return charactersRead;
  }

  public int read(char[] cbuf, int off, int len)
  throws IOException 
  {
    int lineNumber =  ((LineNumberReader) in).getLineNumber();
    int charactersRead = super.read(cbuf, off, len);

    if ( -1 == charactersRead )
    {
      if (autoclose)
      {
	writer.println("]]></line>\n</file>");
        writer.flush();
        writer.close();
      }
    }
    else
    {
      //writer.write(cbuf,off,charactersRead);
      StringBuilder stringBuilder = new StringBuilder();
      for (int offset = 0 ; offset < charactersRead ;  offset++)
      {
       if ('\n' == cbuf[offset])
       {
	 if ( lineNumber > 0 ) 
	 { 
	   stringBuilder.append("]]></line>");
	 }
	 lineNumber++;

	 stringBuilder.append(cbuf[offset]);

	 stringBuilder.append(String.format("<line number=\"%d\"><![CDATA["
					   ,lineNumber
					   )
			     );
       }
       else
       {
	 stringBuilder.append(cbuf[offset]);
       }
      }
      writer.write(stringBuilder.toString());
    }

    return charactersRead;
  }

  public String readLine()
  throws IOException 
  {
    /* FilterReader does not support readLine
       Go direct to the wrapped Reader 
    */
    String line = ((LineNumberReader) in).readLine();

    if ( null == line )
    {
      if (autoclose)
      {
	writer.println("]]></line>\n</file>");
        writer.close();
      }
    }
    else
    {
      writer.println(String.format("<line number=\"%d\"><![CDATA[%s]]>"
                     ,((LineNumberReader) in).getLineNumber()
		     ,line
		     )
		     );
    }


    return line;
  }

}

