package com.barath.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;


import org.springframework.integration.aws.support.S3Session;
import org.springframework.integration.file.remote.AbstractFileInfo;
import org.springframework.integration.file.remote.AbstractRemoteFileStreamingMessageSource;
import org.springframework.integration.file.remote.RemoteFileTemplate;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3StreamingMessageSource extends AbstractRemoteFileStreamingMessageSource<S3ObjectSummary> {

    public S3StreamingMessageSource(RemoteFileTemplate<S3ObjectSummary> template) {
        super(template, null);
    }

    public S3StreamingMessageSource(RemoteFileTemplate<S3ObjectSummary> template,
                                    Comparator<AbstractFileInfo<S3ObjectSummary>> comparator) {

        super(template, comparator);
    }

    @Override
    protected List<AbstractFileInfo<S3ObjectSummary>> asFileInfoList(Collection<S3ObjectSummary> collection) {
        List<AbstractFileInfo<S3ObjectSummary>> canonicalFiles = new ArrayList<AbstractFileInfo<S3ObjectSummary>>();
        for (S3ObjectSummary s3ObjectSummary : collection) {
            canonicalFiles.add(new S3FileInfo(s3ObjectSummary));
        }
        return canonicalFiles;
    }

    @Override
    public String getComponentType() {
        return "aws:s3-inbound-streaming-channel-adapter";
    }

    @Override
    protected AbstractFileInfo<S3ObjectSummary> poll() {
        AbstractFileInfo<S3ObjectSummary> file = super.poll();
        if (file != null) {
            S3Session s3Session = (S3Session) getRemoteFileTemplate().getSession();
            //file.setRemoteDirectory(s3Session.(file.getRemoteDirectory()));
        }
        return file;
    }

}