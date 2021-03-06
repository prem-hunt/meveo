/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.core.inputhandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.meveo.commons.exceptions.ConfigurationException;
import org.meveo.commons.utils.FileUtils;
import org.meveo.config.MeveoFileConfig;
import org.meveo.config.ticket.Ticket;
import org.meveo.core.inputloader.Input;
import org.meveo.core.outputproducer.OutputProducer;
import org.meveo.core.parser.Parser;
import org.meveo.core.process.Processor;

import com.google.inject.Inject;

/**
 * This file handler can be used directly in meveo application as
 * {@link InputHandler} implementation. It uses {@link Parser} to parse tickets
 * and passes them to {@link Processor} (functionality derived from
 * {@link AbstractFileHandler}) and implements simple ticket rejection
 * functionality, by taking source from ticket and appending it to rejected
 * tickets file. It also overrides afterProcessing() method which now not only
 * moves source file to accepted directory, but also rejected tickets file to
 * rejected tickets directory.
 * 
 * @author Ignas Lelys
 * @created Dec 23, 2010
 * 
 */
public class SimpleFileHandler extends AbstractFileInputHandler<Ticket> {

    private static final Logger logger = Logger.getLogger(SimpleFileHandler.class);

    protected File rejectedTicketsFile;

    protected PrintWriter rejectedTicketsWriter;

    /**
     * Constructor with parameters for guice injection.
     * 
     * @param processor
     *            Injected {@link Processor} implementation.
     * @param outputProducer
     *            Injected {@link Processor} implementation.
     * @param parser
     *            Injected {@link Parser} implementation.
     */
    @Inject
    public SimpleFileHandler(Processor<Ticket> processor, OutputProducer outputProducer, Parser<Ticket> parser) {
        super(processor, outputProducer, parser);
    }

    /**
     * @see org.meveo.core.inputhandler.AbstractFileInputHandler#afterProcessing(org.meveo.core.inputloader.Input,
     *      org.meveo.core.inputhandler.TaskExecution)
     */
    @Override
    protected void afterProcessing(Input input, TaskExecution<Ticket> taskExecution) {
        // move original file to accepted files dir
        super.afterProcessing(input, taskExecution);

        // move rejected tickets file
        if (taskExecution.getRejectedTicketsCount() > 0) {
            FileUtils.moveFile(((MeveoFileConfig)config).getRejectedTicketsFilesDirectory(), rejectedTicketsFile,
                    getRejectedTicketsFilename(input.getName()));

        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("No rejected tickets in %s", input.getName()));
            }
        }
    }

    /**
     * @see org.meveo.core.inputhandler.AbstractFileInputHandler#rejectTicket(org.meveo.core.inputloader.Input,
     *      java.lang.Object, java.lang.String)
     */
    @Override
    protected void rejectTicket(Input input, Ticket ticket, String status) {
        try {
            if (rejectedTicketsWriter == null) {
                if (rejectedTicketsFile == null) {
                    rejectedTicketsFile = File.createTempFile(input.getName(), String.valueOf(System
                            .currentTimeMillis()), new File(((MeveoFileConfig)config).getTempFilesDirectory()));
                }
                rejectedTicketsWriter = new PrintWriter(rejectedTicketsFile);
            }
            rejectedTicketsWriter.print(String.valueOf(ticket.getSource()));
            rejectedTicketsWriter.print(((MeveoFileConfig)config).getTicketSeparator());
        } catch (FileNotFoundException e) {
            logger.error("Could not open rejected tickets output file", e);
            throw new ConfigurationException("ould not write to rejected tickets file", e);
        } catch (IOException e) {
            logger.error("Could not create temporary file for rejected tickets", e);
            throw new ConfigurationException("Could not write to rejected tickets file", e);
        }
    }

    /**
     * Takes original name and add.
     * 
     * @param originalName
     *            Original file name
     * @return File name for rejected tickets file.
     */
    private String getRejectedTicketsFilename(String originalName) {
        Format rejectedTicketsFileExtensionFormat = new SimpleDateFormat(((MeveoFileConfig)config).getErrorFileExtension());
        return FileUtils.replaceFilenameExtension(originalName, rejectedTicketsFileExtensionFormat.format(new Date()));
    }

}
