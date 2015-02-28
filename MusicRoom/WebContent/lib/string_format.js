String.prototype.format = function(params)
{
   var content = this;
   for (var i=0; i < params.length; i++)
   {
        var replacement = '{' + i + '}';
        content = content.replace(replacement, params[i]);  
   }
   return content;
}