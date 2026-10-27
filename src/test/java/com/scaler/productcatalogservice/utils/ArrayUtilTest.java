package com.scaler.productcatalogservice.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ArrayUtilTest {

    @Test
    @DisplayName("middle slot: returns the element")
    void getElementByIndex_middleSlot_returnsElement() {
        int[] shelf = {10, 20, 30};

        int result = ArrayUtil.getElementByIndex(shelf, 1);

        assertThat(result).isEqualTo(20);
    }

    @ParameterizedTest
    @CsvSource({"0, 10", "1, 20", "2, 30"})
    void getElementByIndex_goodIndex_returnsElement(int index, int expected) {
        int[] shelf = {10, 20, 30};

        assertThat(ArrayUtil.getElementByIndex(shelf, index)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"-1", "3", "12345"})
    void getElementByIndex_badIndex_throws(int index) {
        int[] shelf = {10, 20, 30};

        assertThrows(IllegalArgumentException.class, () -> ArrayUtil.getElementByIndex(shelf, index));
    }

    @Test
    void getElementByIndex_nullArray_throws() {
        assertThrows(IllegalArgumentException.class, () -> ArrayUtil.getElementByIndex(null, 0));
    }
}
