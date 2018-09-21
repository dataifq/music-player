package com.github.anrimian.musicplayer.data.repositories.music.comparators.composition;

import com.github.anrimian.musicplayer.domain.models.composition.Composition;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.github.anrimian.musicplayer.data.utils.TestDataProvider.fakeComposition;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

public class AlphabeticalDescCompositionComparatorTest {

    @Test
    public void applyOrder() {
        Composition compositionOne = fakeComposition(1, "A");
        Composition compositionTwo = fakeComposition(2, "B");
        Composition compositionThree = fakeComposition(3, "C");

        List<Composition> compositions = asList(compositionOne, compositionTwo, compositionThree);

        Collections.sort(compositions, new AlphabeticalDescCompositionComparator());

        assertEquals(compositionThree, compositions.get(0));
        assertEquals(compositionTwo, compositions.get(1));
        assertEquals(compositionOne, compositions.get(2));
    }
}