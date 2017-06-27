package com.qvik.qvikandroidapp.data.source;

import android.content.Context;

import com.google.common.collect.Lists;
import com.qvik.qvikandroidapp.data.Qvikie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class QvikiesRepositoryTest {

    private final static String QVIKIE_ID = "001";

    private static List<Qvikie> QVIKIES = Lists.newArrayList(
            new Qvikie("Name1", "engineer", "Description1", "00000001", "email1@email.com"),
            new Qvikie("Name2", "engineer", "Description2", "00000002", "email2@email.com")
    );

    private QvikiesRepository qvikiesRepository;

    @Mock
    private QvikiesDataSource qvikiesRemoteDataSource;

    @Mock
    private QvikiesDataSource qvikiesLocalDataSource;

    @Mock
    private Context context;

    @Mock
    private QvikiesDataSource.GetQvikieCallback getQvikieCallback;

    @Mock
    private QvikiesDataSource.LoadQvikiesCallback loadQvikiesCallback;

    @Captor
    private ArgumentCaptor<QvikiesDataSource.LoadQvikiesCallback> qvikiesCallbackCaptor;

    @Captor
    private ArgumentCaptor<QvikiesDataSource.GetQvikieCallback> qvikieCallbackCaptor;

    @Before
    public void setupQvikiesRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        qvikiesRepository = QvikiesRepository.getInstance(
                qvikiesRemoteDataSource, qvikiesLocalDataSource);
    }

    @After
    public void destroyRepositoryInstance() {
        QvikiesRepository.destroyInstance();
    }

    @Test
    public void getQvikies_repositoryCachesAfterFirstApiCall() {
        // Given a setup Captor to capture callbacks
        // When two calls are issued to the qvikies repository
        twoQvikiesLoadCallsToRepository(loadQvikiesCallback);

        // Then qvikies were only requested once from Service API
        verify(qvikiesRemoteDataSource).getQvikies(any(QvikiesDataSource.LoadQvikiesCallback.class));
    }

    @Test
    public void getQvikies_requestsAllQvikiesFromLocalDataSource() {
        // When qvikies are requested from the qvikies repository
        qvikiesRepository.getQvikies(loadQvikiesCallback);

        // Then qvikies are loaded from the local data source
        verify(qvikiesLocalDataSource).getQvikies(any(QvikiesDataSource.LoadQvikiesCallback.class));
    }

    @Test
    public void saveQvikie_savesQvikieToServiceAPI() {
        // Given a stub qvikie
        Qvikie newQvikie = new Qvikie(QVIKIE_ID, "Name1", "engineer", "Description1", "00000001", "email1@email.com");

        // When a qvikie is saved to the qvikies repository
        qvikiesRepository.saveQvikie(newQvikie);

        // Then the service API and persistent repository are called and the cache is updated
        verify(qvikiesRemoteDataSource).saveQvikie(newQvikie);
        verify(qvikiesLocalDataSource).saveQvikie(newQvikie);
        assertThat(qvikiesRepository.cachedQvikies.size(), is(1));
    }

    @Test
    public void getQvikie_requestsSingleQvikieFromLocalDataSource() {
        // When a qvikie is requested from the qvikies repository
        qvikiesRepository.getQvikie(QVIKIE_ID, getQvikieCallback);

        // Then the qvikie is loaded from the database
        verify(qvikiesLocalDataSource).getQvikie(eq(QVIKIE_ID), any(
                QvikiesDataSource.GetQvikieCallback.class));
    }

    @Test
    public void getQvikiesWithDirtyCache_qvikiesAreRetrievedFromRemote() {
        // When calling getQvikies in the repository with dirty cache
        qvikiesRepository.refreshQvikies();
        qvikiesRepository.getQvikies(loadQvikiesCallback);

        // And the remote data source has data available
        setQvikiesAvailable(qvikiesRemoteDataSource, QVIKIES);

        // Verify the qvikies from the remote data source are returned, not the local
        verify(qvikiesLocalDataSource, never()).getQvikies(loadQvikiesCallback);
        verify(loadQvikiesCallback).onQvikiesLoaded(QVIKIES);
    }

    @Test
    public void getQvikiesWithLocalDataSourceUnavailable_qvikiesAreRetrievedFromRemote() {
        // When calling getQvikies in the repository
        qvikiesRepository.getQvikies(loadQvikiesCallback);

        // And the local data source has no data available
        setQvikiesNotAvailable(qvikiesLocalDataSource);

        // And the remote data source has data available
        setQvikiesAvailable(qvikiesRemoteDataSource, QVIKIES);

        // Verify the qvikies from the local data source are returned
        verify(loadQvikiesCallback).onQvikiesLoaded(QVIKIES);
    }

    @Test
    public void getQvikiesWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        // When calling getQvikies in the repository
        qvikiesRepository.getQvikies(loadQvikiesCallback);

        // And the local data source has no data available
        setQvikiesNotAvailable(qvikiesLocalDataSource);

        // And the remote data source has no data available
        setQvikiesNotAvailable(qvikiesRemoteDataSource);

        // Verify no data is returned
        verify(loadQvikiesCallback).onDataNotAvailable();
    }

    @Test
    public void getQvikieWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        // When calling getQvikie in the repository
        qvikiesRepository.getQvikie(QVIKIE_ID, getQvikieCallback);

        // And the local data source has no data available
        setQvikieNotAvailable(qvikiesLocalDataSource, QVIKIE_ID);

        // And the remote data source has no data available
        setQvikieNotAvailable(qvikiesRemoteDataSource, QVIKIE_ID);

        // Verify no data is returned
        verify(getQvikieCallback).onDataNotAvailable();
    }

    @Test
    public void getQvikies_refreshesLocalDataSource() {
        // Mark cache as dirty to force a reload of data from remote data source.
        qvikiesRepository.refreshQvikies();

        // When calling getQvikies in the repository
        qvikiesRepository.getQvikies(loadQvikiesCallback);

        // Make the remote data source return data
        setQvikiesAvailable(qvikiesRemoteDataSource, QVIKIES);

        // Verify that the data fetched from the remote data source was saved in local.
        verify(qvikiesLocalDataSource, times(QVIKIES.size())).saveQvikie(any(Qvikie.class));
    }

    /**
     * Convenience method that issues two calls to the qvikies repository
     */
    private void twoQvikiesLoadCallsToRepository(QvikiesDataSource.LoadQvikiesCallback callback) {
        // When qvikies are requested from repository
        qvikiesRepository.getQvikies(callback); // First call to API

        // Use the Mockito Captor to capture the callback
        verify(qvikiesLocalDataSource).getQvikies(qvikiesCallbackCaptor.capture());

        // Local data source doesn't have data yet
        qvikiesCallbackCaptor.getValue().onDataNotAvailable();


        // Verify the remote data source is queried
        verify(qvikiesRemoteDataSource).getQvikies(qvikiesCallbackCaptor.capture());

        // Trigger callback so qvikies are cached
        qvikiesCallbackCaptor.getValue().onQvikiesLoaded(QVIKIES);

        qvikiesRepository.getQvikies(callback); // Second call to API
    }

    private void setQvikiesNotAvailable(QvikiesDataSource dataSource) {
        verify(dataSource).getQvikies(qvikiesCallbackCaptor.capture());
        qvikiesCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setQvikiesAvailable(QvikiesDataSource dataSource, List<Qvikie> qvikies) {
        verify(dataSource).getQvikies(qvikiesCallbackCaptor.capture());
        qvikiesCallbackCaptor.getValue().onQvikiesLoaded(qvikies);
    }

    private void setQvikieNotAvailable(QvikiesDataSource dataSource, String qvikieId) {
        verify(dataSource).getQvikie(eq(qvikieId), qvikieCallbackCaptor.capture());
        qvikieCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setQvikieAvailable(QvikiesDataSource dataSource, Qvikie qvikie) {
        verify(dataSource).getQvikie(eq(qvikie.getId()), qvikieCallbackCaptor.capture());
        qvikieCallbackCaptor.getValue().onQvikieLoaded(qvikie);
    }
 }
