package com.epam.loader.common.service.search;

import com.epam.loader.common.service.ServiceException;
import com.epam.loader.common.util.CheckingParamsUtil;
import com.epam.loader.common.util.CommonUtilException;
import com.epam.loader.common.util.CommonUtilHolder;
import com.epam.loader.config.condition.DownloadableFile;
import com.epam.loader.plan.strategy.SearchStrategy;
import com.epam.loader.plan.strategy.StrategyException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class HttpSearchService {
  static final String HTTP_PREFIX = "http://";

  public List<DownloadableFile> searchForConfigsLocation( String remoteUrl,
                                                          List<DownloadableFile> searchableServiceNames,
                                                          SearchStrategy searchStrategy ) {
    try {
      List<DownloadableFile> files = searchStrategy.tryToResolveCommandResult(
        askForClientsConfigLocation(
          HTTP_PREFIX + remoteUrl + searchStrategy.getStrategyCommand( searchableServiceNames ) ),
        searchableServiceNames );
      files.forEach( service -> service.setDownloadPath( HTTP_PREFIX + remoteUrl + service.getDownloadPath() ) );

      return files;
    } catch ( StrategyException e ) {
      throw new ServiceException( e );
    }
  }

  String askForClientsConfigLocation( String uri ) {
    try {
      return CheckingParamsUtil.checkParamsWithNullAndEmpty( uri )
        ? new String( IOUtils.toByteArray( CommonUtilHolder.httpCommonUtilInstance().createHttpClient()
        .execute( CommonUtilHolder.httpCommonUtilInstance().createHttpUriRequest( uri ) )
        .getEntity().getContent() ) ) : StringUtils.EMPTY;
    } catch ( IOException | CommonUtilException e ) {
      throw new ServiceException( e );
    }
  }
}
