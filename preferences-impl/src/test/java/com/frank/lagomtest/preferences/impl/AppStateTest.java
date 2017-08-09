package com.frank.lagomtest.preferences.impl;

import com.frank.lagomtest.preferences.api.AppStatus;
import com.frank.lagomtest.preferences.api.model.App;
import com.frank.lagomtest.preferences.api.model.BlockContainer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ftorriani
 */
public class AppStateTest {

    @Test
    public void testAddBlockContainer() {

        App app = App.builder().
                appId( "1" ).
                build();
        AppState state = AppState.builder().
                app( app ).
                status( AppStatus.DRAFT ).build();
        assertThat( state.getById( "11" ).isPresent() ).isFalse();

        // Create a add container

        BlockContainer container = BlockContainer.builder().
                blockContainerId( "11" ).
                description( "first add" ).
                iconizable( true ).
                build();

        state = AppState.builder( state ).add( container ).build();

        assertThat( state.getById( "11" ).get() ).isEqualTo( container );
        assertThat( state.getById( "10" ).isPresent() ).isFalse();

        // Now remove

        state = AppState.builder( state ).remove( container ).build();
        assertThat( state.getById( "11" ).isPresent() ).isFalse();

    }


}