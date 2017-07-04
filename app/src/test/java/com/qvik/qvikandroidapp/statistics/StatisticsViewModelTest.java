package com.qvik.qvikandroidapp.statistics;


import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.common.collect.Lists;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link StatisticsViewModel}
 */
public class StatisticsViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static List<Qvikie> QVIKIES;

    @Mock
    private QvikiesRepository qvikiesRepository;

    @Captor
    private ArgumentCaptor<QvikiesDataSource.LoadQvikiesCallback> loadQvikiesCallbackCaptor;

    private StatisticsViewModel statisticsViewModel;

    @Before
    public void setupStatisticsViewModel() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        statisticsViewModel = new StatisticsViewModel(mock(Application.class), qvikiesRepository);

        // We initialise the qvikies to 3, with two engineers and one designer
        QVIKIES = Lists.newArrayList(
                new Qvikie("Name1", "engineer", "Description1", "00000001", "email1@email.com"),
                new Qvikie("Name2", "engineer", "Description2", "00000002", "email2@email.com"),
                new Qvikie("Name3", "designer", "Description3", "00000003", "email3@email.com")
        );
    }

    @Test
    public void loadEmptyQvikiesFromRepository_EmptyResults() {
        // Given an initialized StatisticsViewModel with no qvikies
        QVIKIES.clear();

        // When loading of Qvikies is requested
        statisticsViewModel.loadStatistics();

        // Callback is captured and invoked with stubbed qvikies
        verify(qvikiesRepository).getQvikies(loadQvikiesCallbackCaptor.capture());
        loadQvikiesCallbackCaptor.getValue().onQvikiesLoaded(QVIKIES);

        // Then the results are empty
        assertThat(statisticsViewModel.empty.get(), is(true));
    }

    @Test
    public void loadNonEmptyQvikiesFromRepository_NonEmptyResults() {
        // When loading of Qvikies is requested
        statisticsViewModel.loadStatistics();

        // Callback is captured and invoked with stubbed qvikies
        verify(qvikiesRepository).getQvikies(loadQvikiesCallbackCaptor.capture());
        loadQvikiesCallbackCaptor.getValue().onQvikiesLoaded(QVIKIES);

        // Then the results are not empty
        assertThat(statisticsViewModel.empty.get(), is(false));
    }

    @Test
    public void loadStatisticsWhenQvikiesAreUnavailable_CallErrorToDisplay() {
        // When statistics are loaded
        statisticsViewModel.loadStatistics();

        // And qvikies' data isn't available
        verify(qvikiesRepository).getQvikies(loadQvikiesCallbackCaptor.capture());
        loadQvikiesCallbackCaptor.getValue().onDataNotAvailable();

        // Then an error message is shown
        assertEquals(statisticsViewModel.empty.get(), true);
        assertEquals(statisticsViewModel.error.get(), true);
    }
}
