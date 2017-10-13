package com.epam.spring.service.download;

import com.epam.spring.config.SshCredentials;
import com.epam.spring.exception.CommonUtilException;
import com.epam.spring.executor.DelegatingExecutorService;
import com.epam.spring.plan.DownloadPlan;
import com.epam.spring.util.CommonUtilHolder;
import com.epam.spring.util.FileCommonUtil;
import com.epam.spring.util.SshCommonUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.concurrent.CompletionException;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { CommonUtilHolder.class, FileCommonUtil.class } )
public class SshDownloadServiceTest {
  @Test
  public void askForClientsConfigsWhenCommandIsNullShouldReturnEmptyString() throws Exception {
    SshDownloadService sshDownloadService = new SshDownloadService();
    Assert.assertTrue( sshDownloadService.askForClientsConfigs( StringUtils.EMPTY, null ).isEmpty() );
  }

  @SuppressWarnings( "unchecked" )
  @Test( expected = CompletionException.class )
  public void searchForConfigsWhenCommonUtilRaiseExceptionShouldRaiseCompletionException() throws Exception {
    SshDownloadService sshDownloadService = new SshDownloadService();
    SshCommonUtil sshCommonUtil = Mockito.mock( SshCommonUtil.class );
    DownloadPlan.LoadPathConfig loadPathConfig = Mockito.mock( DownloadPlan.LoadPathConfig.class );
    PowerMockito.mockStatic( CommonUtilHolder.class );

    Mockito.when( CommonUtilHolder.sshCommonUtilInstance() ).thenReturn( sshCommonUtil );
    Mockito.when( sshCommonUtil.downloadConfigs( Mockito.any( SshCredentials.class ), Mockito.anyString(),
      Mockito.anyInt(), Mockito.anyString() ) )
      .thenThrow( CommonUtilException.class );
    Mockito.when( loadPathConfig.getLoadedFiles() ).thenReturn( Collections.singletonList( "some" ) );
    Mockito.when( loadPathConfig.getCompositeHost() ).thenReturn( "some" );

    try ( DelegatingExecutorService delegatingExecutorService = new DelegatingExecutorService( 1 ) ) {
      sshDownloadService
        .loadConfigsFromCommand( "some", loadPathConfig, delegatingExecutorService.getExecutorService() ).join();
    }

  }

  @SuppressWarnings( "unchecked" )
  @Test
  public void searchForConfigsLocationWhenCommonUtilExecuteCommandSuccessfullyShouldReturnTrue() throws Exception {
    SshDownloadService sshDownloadService = new SshDownloadService();
    SshCommonUtil sshCommonUtil = Mockito.mock( SshCommonUtil.class );
    DownloadPlan.LoadPathConfig loadPathConfig = Mockito.mock( DownloadPlan.LoadPathConfig.class );
    PowerMockito.mockStatic( CommonUtilHolder.class );
    PowerMockito.mockStatic( FileCommonUtil.class );

    Mockito.when( CommonUtilHolder.sshCommonUtilInstance() ).thenReturn( sshCommonUtil );
    PowerMockito.doNothing()
      .when( FileCommonUtil.class, "writeStringToFile", Mockito.anyString(), Mockito.anyString() );
    Mockito.when( sshCommonUtil.downloadConfigs( Mockito.any( SshCredentials.class ), Mockito.anyString(),
      Mockito.anyInt(), Mockito.anyString() ) )
      .thenReturn( "some" );
    Mockito.when( loadPathConfig.getLoadedFiles() ).thenReturn( Collections.singletonList( "some" ) );
    Mockito.when( loadPathConfig.getCompositeHost() ).thenReturn( "some" );

    try ( DelegatingExecutorService delegatingExecutorService = new DelegatingExecutorService( 1 ) ) {
      Assert.assertTrue( sshDownloadService
        .loadConfigsFromCommand( "some", loadPathConfig, delegatingExecutorService.getExecutorService() ).get() );
    }
  }
}