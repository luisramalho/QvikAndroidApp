package com.qvik.qvikandroidapp.qvikies;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.content.res.Resources;

import com.google.common.collect.Lists;
import com.qvik.qvikandroidapp.TestUtils;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource.LoadQvikiesCallback;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of {@link QvikiesViewModel}
 */
public class QvikiesViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static List<Qvikie> QVIKIES;

    private static List<Qvikie> EMPTY;

    @Mock
    private QvikiesRepository qvikiesRepository;

    @Mock
    private Application context;

    @Captor
    private ArgumentCaptor<LoadQvikiesCallback> loadQvikiesCallbackCaptor;

    private QvikiesViewModel qvikiesViewModel;

    @Before
    public void setupQvikiesViewModel() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        setupContext();

        // Get a reference to the class under test
        qvikiesViewModel = new QvikiesViewModel(context, qvikiesRepository);

        // We initialise the qvikies to 3, with two engineers and one designer
        QVIKIES = Lists.newArrayList(
                new Qvikie("Name1", "engineer", "Description1", "00000001", "email1@email.com"),
                new Qvikie("Name2", "engineer", "Description2", "00000002", "email2@email.com"),
                new Qvikie("Name3", "designer", "Description3", "00000003", "email3@email.com")
        );

        EMPTY = new ArrayList<>();
    }

    private void setupContext() {
        when(context.getApplicationContext()).thenReturn(context);

        when(context.getResources()).thenReturn(mock(Resources.class));
    }

    @Test
    public void loadAllQvikiesFromRepository_dataLoaded() {
        // Given an initialized QvikiesViewModel with initialized qvikies
        // When loading of Qvikies is requested
        qvikiesViewModel.setFiltering(QvikiesFilterType.ALL_QVIKIES);
        qvikiesViewModel.loadQvikies(true);

        // Callback is captured and invoked with stubbed qvikies
        verify(qvikiesRepository).getQvikies(loadQvikiesCallbackCaptor.capture());


        // Then progress indicator is shown
        assertTrue(qvikiesViewModel.dataLoading.get());
        loadQvikiesCallbackCaptor.getValue().onQvikiesLoaded(QVIKIES);

        // Then progress indicator is hidden
        assertFalse(qvikiesViewModel.dataLoading.get());

        // And data loaded
        assertFalse(qvikiesViewModel.items.isEmpty());
        assertTrue(qvikiesViewModel.items.size() == 3);
    }

    @Test
    public void loadEngineerQvikiesFromRepositoryAndLoadIntoView() {
        // Given an initialized QvikiesViewModel with initialized qvikies
        // When loading of Qvikies is requested
        qvikiesViewModel.setFiltering(QvikiesFilterType.ENGINEERS);
        qvikiesViewModel.loadQvikies(true);

        // Callback is captured and invoked with stubbed qvikies
        verify(qvikiesRepository).getQvikies(loadQvikiesCallbackCaptor.capture());
        loadQvikiesCallbackCaptor.getValue().onQvikiesLoaded(QVIKIES);

        // Then progress indicator is hidden
        assertFalse(qvikiesViewModel.dataLoading.get());

        // And data loaded
        assertFalse(qvikiesViewModel.items.isEmpty());
        assertTrue(qvikiesViewModel.items.size() == 2);
    }

    @Test
    public void loadDesignerQvikiesFromRepositoryAndLoadIntoView() {
        // Given an initialized QvikiesViewModel with initialized qvikies
        // When loading of Qvikies is requested
        qvikiesViewModel.setFiltering(QvikiesFilterType.DESIGNERS);
        qvikiesViewModel.loadQvikies(true);

        // Callback is captured and invoked with stubbed qvikies
        verify(qvikiesRepository).getQvikies(loadQvikiesCallbackCaptor.capture());
        loadQvikiesCallbackCaptor.getValue().onQvikiesLoaded(QVIKIES);

        // Then progress indicator is hidden
        assertFalse(qvikiesViewModel.dataLoading.get());

        // And data loaded
        assertFalse(qvikiesViewModel.items.isEmpty());
        assertTrue(qvikiesViewModel.items.size() == 1);
    }

    @Test
    public void loadAllQvikiesFromRepositoryAndDeleteAll() {
        // Given an initialized QvikiesViewModel with initialized qvikies
        // When loading of Qvikies is requested
        qvikiesViewModel.setFiltering(QvikiesFilterType.ALL_QVIKIES);
        qvikiesViewModel.loadQvikies(true);

        // Callback is captured and invoked with stubbed qvikies
        verify(qvikiesRepository).getQvikies(loadQvikiesCallbackCaptor.capture());

        // Then progress indicator is shown
        assertTrue(qvikiesViewModel.dataLoading.get());
        loadQvikiesCallbackCaptor.getValue().onQvikiesLoaded(QVIKIES);

        // Then progress indicator is hidden
        assertFalse(qvikiesViewModel.dataLoading.get());

        // And data loaded
        assertFalse(qvikiesViewModel.items.isEmpty());
        assertTrue(qvikiesViewModel.items.size() == 3);

        // When the deletion of qvikies is requested
        qvikiesViewModel.deleteAllQvikies();

        // Then progress indicator is shown
        assertTrue(qvikiesViewModel.dataLoading.get());
        loadQvikiesCallbackCaptor.getValue().onQvikiesLoaded(EMPTY);

        // Then progress indicator is hidden
        assertFalse(qvikiesViewModel.dataLoading.get());

        // And all qvikies are deleted
        assertTrue(qvikiesViewModel.items.isEmpty());
        assertFalse(qvikiesViewModel.items.size() == 3);
    }

    @Test
    public void clickOnDeleteAllQvikies_DeletesQvikies() {
        // When all qvikies are deleted
        qvikiesViewModel.deleteAllQvikies();

        // Then repository is called and the view is notified
        verify(qvikiesRepository).deleteAllQvikies();
        verify(qvikiesRepository).getQvikies(any(LoadQvikiesCallback.class));
    }

    @Test
    public void clickOnAddQvikie_ShowsAddTaskUi() {

        Observer<Void> observer = mock(Observer.class);

        qvikiesViewModel.getNewQvikieEvent().observe(TestUtils.TEST_OBSERVER,
                observer);

        // When adding a new qvikie
        qvikiesViewModel.addQvikie();

        // Then the event is triggered
        verify(observer).onChanged(null);
    }
}
