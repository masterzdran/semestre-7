var utils = {
    //propriedade:function(){},

    first: function (array, pred) {
        for (var i = 0; i < array.length; ++i) {
            if (pred(array[i]))
                return array[i];
        }
        return undefined;
    },

    filter: function (array, pred) {
        var result = [];
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

    //and normal
    and_v1: function (pred, pred2) {
        return function (n) {
            return (pred(n) && pred2(n));
        };
    },

    map: function (array, pred) {
        var result = [];
        for (var i = 0; i < array.length; ++i) {
            result.push(pred(array[i]));
        }
        return result;
    },

    //and com suporte para lista variavel de argumentos
    and_v2: function () {
        var args = arguments;
        return function (n) {
            if (args.length <= 0) return false;
            var result = true;
            for (var i = 0; i < args.length; ++i)
                result &= args[i](n);
            return result;
        };
    },

    //realizar all (array, predicate), retorna true se todos os argumentos satisfazem o predicado
    all: function (array, pred) {
        for (var i = 0; i < array.length; ++i)
            if (!pred(array[i])) return false;
        return true;          
    },

    //realizar any (array, predicate), retorna true se pelo menos 1 dos argumentos satisfaz o predicado
    any: function (array, pred) {
        for (var i = 0; i < array.length; ++i)
            if (pred(array[i])) return true;
        return false;          
    },

    //realizar and usando o all
    and: function () {
        var args = arguments;
        return function (n) {
            if (args.length <= 0) return false; //teste 11 falha
            return utils.all(args, function(func){return func(n);});
        };
    },


    //ver underscoreJS

}