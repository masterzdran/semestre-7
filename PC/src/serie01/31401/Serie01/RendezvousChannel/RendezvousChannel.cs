using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
/*
 * Implemente   em   C#   o   sincronizador   rendezvous   channel,   com   base   na   
 * classe   genérica RendezvousChannel<S,R>.  
 * O  rendezvous  channel  serve  para  sincronizar  a  comunicação  entre threads cliente e threads servidoras, 
 * com a seguinte semântica: 
 * -> 
 */


namespace Serie01
{
    class RendezvousChannel<S,R>
    {
        public RendezvousChannel();
        /*
         * As  threads  cliente  realizam  pedidos  de   serviço  invocando  o   método   
         *      bool  Request(S service,  int  timeout,  out  R  response).  
         * O  objecto,  do  tipo  S,  passado  através  do parâmetro  service  descreve  o  pedido  de  serviço.  
         * Se  o  serviço  for  executado  com  sucesso  por uma thread servidora, este método devolve true 
         * e a resposta ao pedido de serviço, um objecto do tipo R, é passada através do parâmetro response; 
         * Se pedido de serviço não for aceite de imediato, por  não  existir  nenhuma  thread  servidora  disponível,
         * a  thread  cliente  fica  bloqueada  até  que  o pedido  de  serviço  seja  aceite,  a  thread  cliente  seja
         * interrompida  ou  expire  o  limite  de  tempo especificado  através  do  parâmetro  timeout.  
         * (Quando  existe  desistência  o  método  Request devolve false.) 
         * Dado que não está prevista nenhuma forma de interromper o processamento de um pedido de serviço já aceite 
         * por uma thread servidora, as threads cliente não poderão desistir, devido  a  interrupção  ou  timeout,  
         * após  terem  iniciado  o  rendezvous  com  uma  thread  servidora, devendo  esperar  incondicionalmente  que
         * o  serviço  seja  concluído,  isto  é,  a  thread  servidora invoque o método Reply, com o respectivo 
         * rendezvous token.
         */
        public bool Request(S service, int timeout, out  R response);
        /* Sempre que uma thread servidora estiver em condições de processar pedidos de serviço, invoca o método 
         *      object Accept (int timeout, out S service). 
         * Quando um pedido de serviço é aceite,  a  descrição  do  pedido  de  serviço  é  passado  através  do  
         * parâmetro  de  saída  service  e  o método  Accept  devolve  também  um  rendezvous  token  
         * (i.e.,  um  objecto  opaco,  cujo  tipo  é definido  pela  implementação)  para  identificar  um  
         * rendezvous  particular.  
         * Quando  não  existe nenhum pedido de serviço pendente, a thread servidora fica bloqueada até que seja 
         * solicitado um pedido de serviço, seja interrompida ou expire o limite de tempo especificado através do 
         * parâmetro timeout.  
         * (Este  método  deve  devolver  null  como  rendezvous  token  para  indicar  que  a  thread servidora 
         * retornou por desistência.)          
         */
        public object Accept(int timeout, out S service);

        /*Quando  uma  thread  servidora  quer  indicar  a  conclusão  de  um  serviço  particular  
         * (definido  pelo respectivo   rendezvous   token)   e   devolver   o   respectivo   resultado,
         * invoca   o   método   
         *      void Reply(object rendezVousToken, R response). 
         * Através do primeiro parâmetro é passada a identificação do rendezvous e através do parâmetro response
         * o objecto do tipo R, que contém a resposta ao pedido de serviço.
         */
        public void Reply(object rendezVousToken, R response);
    }
}
