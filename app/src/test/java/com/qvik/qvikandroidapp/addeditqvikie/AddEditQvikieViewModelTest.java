package com.qvik.qvikandroidapp.addeditqvikie;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;

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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link AddEditQvikieViewModel}.
 */
public class AddEditQvikieViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private QvikiesRepository qvikiesRepository;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument
     * values and use them to perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<QvikiesDataSource.GetQvikieCallback> getQvikieCallbackCaptor;

    private AddEditQvikieViewModel addEditQvikieViewModel;

    @Before
    public void setupAddEditQvikieViewModel() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        addEditQvikieViewModel = new AddEditQvikieViewModel(
                mock(Application.class), qvikiesRepository);
    }

    @Test
    public void saveNewQvikieToRepository_showsSuccessMessageUi() {
        // When the ViewModel is asked to save a qvikie
        addEditQvikieViewModel.name.set("Name");
        addEditQvikieViewModel.title.set("engineer");
        addEditQvikieViewModel.description.set("Description");
        addEditQvikieViewModel.phoneNumber.set("0000-0000");
        addEditQvikieViewModel.email.set("name@qvik.fi");
        addEditQvikieViewModel.saveQvikie();

        // Then a qvikie is saved in the repository and the view updated
        verify(qvikiesRepository).saveQvikie(any(Qvikie.class)); // saved to the model
    }

    @Test
    public void populateQvikie_callsRepoAndUpdatesView() {
        Qvikie testQvikie = new Qvikie(
                "Name",
                "engineer",
                "Description",
                "0000-0000",
                "name@qvik.fi"
        );

        // Get a reference to the class under test
        addEditQvikieViewModel = new AddEditQvikieViewModel(
                mock(Application.class), qvikiesRepository);

        // When the ViewModel is asked to populate an existing qvikie
        addEditQvikieViewModel.start(testQvikie.getId());

        // Then the qvikie repository is queried and the view updated
        verify(qvikiesRepository).getQvikie(eq(testQvikie.getId()),
                getQvikieCallbackCaptor.capture());

        // Simulate callback
        getQvikieCallbackCaptor.getValue().onQvikieLoaded(testQvikie);

        // Verify the fields were updated
        assertThat(addEditQvikieViewModel.name.get(), is(testQvikie.getName()));
        assertThat(addEditQvikieViewModel.title.get(), is(testQvikie.getTitle()));
        assertThat(addEditQvikieViewModel.description.get(), is(testQvikie.getDescription()));
        assertThat(addEditQvikieViewModel.phoneNumber.get(), is(testQvikie.getPhoneNumber()));
        assertThat(addEditQvikieViewModel.email.get(), is(testQvikie.getEmail()));
    }
}
