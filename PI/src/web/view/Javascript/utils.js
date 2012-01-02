/**
 * @author nac
 */
var utils = {
    //propriedade:function(){},
    first: function (array, predicate) {
		if(array == 'undefined' || array == 'Empty' ) return undefined;
        for(var i = 0; i < array.length; ++i) {
            if (predicate(array[i])) return array[i];
        }
        return undefined;
    },
	
	map:function(array, predicate)
	{
		if(array == 'undefined' || array == 'Empty' ) return undefined;
		
		var tmp = [];
		for(var i = 0; i < array.length; ++i) {
            tmp.push(predicate(array[i]));
        }
		return tmp;
	},
	
	
	filter:function(array, predicate){ 
		if(array == 'undefined' || array == 'Empty' ) return undefined;
		
		var tmp = [];
		for(var i = 0; i < array.length; ++i) {
            if (predicate(array[i]))  tmp.push(array[i]);
        }
		return tmp;
	},
	not: function(predicate){
		return function(i){
			return !predicate(i);
		}
	},
	and:function(predicate, result){
		return function(n){
			return predicate(n)&&result(n);
		}
	},
	
	
};