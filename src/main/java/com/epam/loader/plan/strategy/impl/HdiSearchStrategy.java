package com.epam.loader.plan.strategy.impl;

import com.epam.loader.config.condition.DownloadableFile;
import com.epam.loader.plan.strategy.SearchStrategy;
import com.epam.loader.plan.strategy.StrategyException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component( "hdi-rest-strategy" )
public class HdiSearchStrategy implements SearchStrategy {
  @Override
  public String getStrategyCommand( List<DownloadableFile> searchableServiceNames ) {
    return "clusters/";
  }

  @Override
  public List<DownloadableFile> resolveCommandResult( String commandResult,
                                                      List<DownloadableFile> searchableServiceNames )
    throws StrategyException {
    String clusterName = extractClusterNameFromCommandResult( commandResult );
    if ( !clusterName.isEmpty() ) {
      searchableServiceNames.forEach( service -> service
        .setDownloadPath( "clusters/" + clusterName + "/services/" + service.getServiceName().toUpperCase()
          + "/components/" + service.getServiceName().toUpperCase() + "_CLIENT?format=client_config_tar" ) );

      return searchableServiceNames;
    }

    return Collections.emptyList();
  }

  private String extractClusterNameFromCommandResult( String commandResult ) throws StrategyException {
    try {
      return new JSONObject( commandResult ).getJSONArray( "items" ).getJSONObject( 0 ).getJSONObject( "Clusters" )
        .getString( "cluster_name" );
    } catch ( JSONException e ) {
      throw new StrategyException( e );
    }
  }
}
