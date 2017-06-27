package com.qvik.qvikandroidapp.qvikiedetail;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.content.res.Resources;

import com.qvik.qvikandroidapp.R;
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of {@link QvikieDetailViewModel}
 */
public class QvikieDetailViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static final String NAME_TEST = "name";

    private static final String TITLE_TEST = "title";

    private static final String DESCRIPTION_TEST = "description";

    private static final String PHONE_NUMBER_TEST = "00000000";

    private static final String EMAIL_TEST = "john.doe@email.com";

    private static final String NO_DATA_STRING = "NO_DATA_STRING";

    private static final String NO_DATA_DESC_STRING = "NO_DATA_DESC_STRING";

    @Mock
    private QvikiesRepository qvikiesRepository;

    @Mock
    private Application context;

    @Mock
    private QvikiesDataSource.GetQvikieCallback repositoryCallback;

    @Mock
    private QvikiesDataSource.GetQvikieCallback viewModelCallback;

    @Captor
    private ArgumentCaptor<QvikiesDataSource.GetQvikieCallback> getQvikieCallbackCaptor;

    private QvikieDetailViewModel qvikieDetailViewModel;

    private Qvikie qvikie;

    @Before
    public void setupTasksViewModel() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        setupContext();

        qvikie = new Qvikie(
                NAME_TEST,
                TITLE_TEST,
                DESCRIPTION_TEST,
                PHONE_NUMBER_TEST,
                EMAIL_TEST
        );

        // Get a reference to the class under test
        qvikieDetailViewModel = new QvikieDetailViewModel(context, qvikiesRepository);
    }

    private void setupContext() {
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getString(R.string.no_data)).thenReturn(NO_DATA_STRING);
        when(context.getString(R.string.no_data_description)).thenReturn(NO_DATA_DESC_STRING);
        when(context.getResources()).thenReturn(mock(Resources.class));
    }

    @Test
    public void getQvikieFromRepositoryAndLoadIntoView() {
        setupViewModelRepositoryCallback();

        // Then verify that the view was notified
        assertEquals(qvikieDetailViewModel.qvikie.get().getName(), qvikie.getName());
        assertEquals(qvikieDetailViewModel.qvikie.get().getTitle(), qvikie.getTitle());
        assertEquals(qvikieDetailViewModel.qvikie.get().getDescription(), qvikie.getDescription());
        assertEquals(qvikieDetailViewModel.qvikie.get().getPhoneNumber(), qvikie.getPhoneNumber());
        assertEquals(qvikieDetailViewModel.qvikie.get().getEmail(), qvikie.getEmail());
    }

    @Test
    public void QvikieDetailViewModel_repositoryError() {
        // Given an initialized ViewModel with a qvikie
        viewModelCallback = mock(QvikiesDataSource.GetQvikieCallback.class);

        qvikieDetailViewModel.start(qvikie.getId());

        // Use a captor to get a reference for the callback.
        verify(qvikiesRepository).getQvikie(eq(qvikie.getId()), getQvikieCallbackCaptor.capture());

        // When the repository returns an error
        getQvikieCallbackCaptor.getValue().onDataNotAvailable(); // Trigger callback error

        // Then verify that data is not available
        assertFalse(qvikieDetailViewModel.isDataAvailable());
    }

    @Test
    public void QvikieDetailViewModel_repositoryNull() {
        setupViewModelRepositoryCallback();

        // When the repository returns a null qvikie
        getQvikieCallbackCaptor.getValue().onQvikieLoaded(null); // Trigger callback error

        // Then verify that data is not available
        assertFalse(qvikieDetailViewModel.isDataAvailable());

        // Then qvikie detail UI is shown
        assertThat(qvikieDetailViewModel.qvikie.get(), is(nullValue()));
    }

    private void setupViewModelRepositoryCallback() {
        // Given an initialized ViewModel with a qvikie
        viewModelCallback = mock(QvikiesDataSource.GetQvikieCallback.class);

        qvikieDetailViewModel.start(qvikie.getId());

        // Use a captor to get a reference for the callback.
        verify(qvikiesRepository).getQvikie(eq(qvikie.getId()), getQvikieCallbackCaptor.capture());

        getQvikieCallbackCaptor.getValue().onQvikieLoaded(qvikie); // Trigger callback
    }
}
