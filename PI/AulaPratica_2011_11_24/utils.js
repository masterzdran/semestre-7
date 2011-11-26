

var utils = {
    first: function (array, pred) {
        for (var i = 0; i < array.length; ++i) {
            if (pred(array[i]))
                return array[i];
        }
        return undefined;
    },

    filter: function (array, pred) {
        var result = new Array();
        for (var i = 0; i < array.length; ++i) {
            if (pred(array[i])) {
                result.push(array[i]);
            }
        }
        return result;

    },

    not: function (pred) {
        return function (n) {
            return !(pred(n));
        };
    },

    and: function (pred, pred2) {
        return function (n) {
            return (pred(n) && pred2(n));
        };
    },

    map: function (array, pred) {
        var result = new Array();
        for (var i = 0; i < array.length; ++i) {
            result.push(pred(array[i]));
        }
        return result;
    }

}