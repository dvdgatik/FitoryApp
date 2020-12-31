package com.dimakers.fitoryapp.api.apiServices;

import com.dimakers.fitoryapp.api.models.ActivarSubscripcionFree;
import com.dimakers.fitoryapp.api.models.Actividad;
import com.dimakers.fitoryapp.api.models.ActividadClub;
import com.dimakers.fitoryapp.api.models.ActividadHorario;
import com.dimakers.fitoryapp.api.models.Asistencia;
import com.dimakers.fitoryapp.api.models.CheckPhoneResponse;
import com.dimakers.fitoryapp.api.models.Ciudad;
import com.dimakers.fitoryapp.api.models.Cliente;
import com.dimakers.fitoryapp.api.models.Club;
import com.dimakers.fitoryapp.api.models.Estado;
import com.dimakers.fitoryapp.api.models.EvaluacionSucursal;
import com.dimakers.fitoryapp.api.models.Favorito;
import com.dimakers.fitoryapp.api.models.Fecha;
import com.dimakers.fitoryapp.api.models.GetSubscripcionesResponse;
import com.dimakers.fitoryapp.api.models.MetodosPago;
import com.dimakers.fitoryapp.api.models.Objetivos;
import com.dimakers.fitoryapp.api.models.PerfilSucursal;
import com.dimakers.fitoryapp.api.models.PromedioEvaluacion;
import com.dimakers.fitoryapp.api.models.RegistrarCelularResponse;
import com.dimakers.fitoryapp.api.models.RegistroAsistencia;
import com.dimakers.fitoryapp.api.models.RevisionSubscripcionFree;
import com.dimakers.fitoryapp.api.models.Servicio;
import com.dimakers.fitoryapp.api.models.ServicioClub;
import com.dimakers.fitoryapp.api.models.Sesion;
import com.dimakers.fitoryapp.api.models.SubscripcionMes;
import com.dimakers.fitoryapp.api.models.SubscripcionPojo;
import com.dimakers.fitoryapp.api.models.SubscripcionesGratis;
import com.dimakers.fitoryapp.api.models.Sucursal;
import com.dimakers.fitoryapp.api.models.Suscripcion;
import com.dimakers.fitoryapp.api.models.UpdatePhoneResponse;
import com.dimakers.fitoryapp.api.models.User;
import com.dimakers.fitoryapp.api.models.UserLogin;
import com.dimakers.fitoryapp.api.models.VerifyPhoneResponse;
import com.dimakers.fitoryapp.api.models.Visita;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FitoryService {
    /**
     * API para login de usuario
     */
    @FormUrlEncoded
    @POST("userLogin/")
    Call<UserLogin> iniciarSesion(@Field("username") String usuario, @Field("password") String password);

    /**
     * API para crear usuario DJANGO
     */
    @FormUrlEncoded
    @POST("api/user/")
    Call<User> crearUsuario(@Field("username") String usuario,
                            @Field("email") String correo,
                            @Field("password") String contrasena);

    /**
     * API para registrar usuario Cliente
     */
    @FormUrlEncoded
    @POST("api/cliente/")
    Call<Cliente> crearCliente(@Field("user") int user,
                               @Field("nombre") String nombre,
                               @Field("apellido") String apellido,
                               @Field("hombre") boolean hombre,
                               @Field("mujer") boolean mujer,
                               @Field("telefono") String telefono);

    /**
     * API para registrar usuario Cliente con Google id
     */
    @FormUrlEncoded
    @POST("api/cliente/")
    Call<Cliente> crearClienteGoogle(@Field("user") int user,
                                     @Field("nombre") String nombre,
                                     @Field("apellido") String apellido,
                                     @Field("hombre") boolean hombre,
                                     @Field("mujer") boolean mujer,
                                     @Field("idGoogle") String idGoogle);

    /**
     * API para registrar usuario Cliente con Facebook id
     */
    @FormUrlEncoded
    @POST("api/cliente/")
    Call<Cliente> crearClienteFacebook(@Field("user") int user,
                                       @Field("nombre") String nombre,
                                       @Field("apellido") String apellido,
                                       @Field("hombre") boolean hombre,
                                       @Field("mujer") boolean mujer,
                                       @Field("idFacebook") String idFacbook);

    /**
     *  API para crear usuario Cliente FULL
     */
//    @POST("api/cliente/")
//    Call<Cliente> crearCliente(@Field("user") int user,
//                               @Field("nombre") String nombre,
//                               @Field("apellido") String apellido,
//                               @Field("hombre") boolean hombre,
//                               @Field("mujer") boolean mujer,
//                               @Field("salud") boolean salud,
//                               @Field("convivir") boolean convivir,
//                               @Field("vermeBien") boolean vermeBien,
//                               @Field("diversion") boolean diversion,
//                               @Field("estado") int estado,
//                               @Field("ciudad") int ciudad,
//                               @Field("ubicacion") boolean ubicacon,
//                               @Field("bluetooth") boolean bluetooth,
//                               @Field("idFacebook") String idFacebook,
//                               @Field("idGoogle") String idGoogle,
//                               @Field("idCustomer") String idCustomer,
//                               @Field("foto") String foto,
//                               @Field("plan") int plan);

    /**
     * API para actualizar el objetivo según la preferencia del cliente
     */
    @PATCH("api/cliente/{id}/")
    Call<ResponseBody> actualizarObjetivo(@Path("id") String clienteId, @Body Objetivos objetivos);

    /**
     * API para obtener todos los estados
     */
    @GET("api/estado/")
    Call<Estado> obtenerEstados();


    /**
     * API para obtener todas las ciudades por estado
     */
    @GET("api/ciudad/")
    Call<Ciudad> obtenerCiudades(@Query("estado__id") int estadoId);

    /**
     * API para obtener todas las sucursales por ciudadid
     */
    @GET("api/sucursal/")
    Call<Sucursal> obtenerSucursales(@Query("activa") boolean activado,@Query("club__activado") boolean activada, @Query("ciudad") int ciudadID);

    /**
     * API para obtener todas las sucursales
     */
    @GET("api/sucursal/")
    Call<Sucursal> obtenerSucursalesV2(@Query("activa") boolean activado,@Query("club__activado") boolean activada);

    /**
     * API para obtener una sucursal por su id
     */
    @GET("api/sucursal/{id}/")
    Call<Sucursal> obtenerSucursal(@Path("id") int sucursalId, @Query("activa") boolean activado);

    /**
     * API para obtener la información del cliente por user_id
     */
    @GET("api/cliente/")
    Call<Cliente> obtenerCliente(@Query("user") int userId);

    /**
     * API para obtener la información del cliente por facebook id
     */
    @GET("api/cliente/")
    Call<Cliente> obtenerClientesFacebook(@Query("idFacebook") String facebookId);

    /**
     * API para obtener el id de una ciudad por su nombre
     */
    @GET("api/ciudad/")
    Call<Ciudad> obtenerCiudadId(@Query("nombre") String nombreCiudad);

    /**
     * API para encontrar usuario registrados por google id
     */
    @GET("api/cliente/")
    Call<Cliente> obtenerClientesGoogle(@Query("idGoogle") String idGoogle);

    /**
     * API para obtener una ciudad por su id
     */
    @GET("api/ciudad/{id}/")
    Call<Ciudad> obtenerCiudad(@Path("id") int ciudadId);

    /**
     * API para obtener un estado por su id
     */
    @GET("api/estado/{id}/")
    Call<Estado> obtenerEstado(@Path("id") int estadoId);

    /**
     * API para obtener un club por su id
     */
    @GET("api/club/{id}/")
    Call<Club> obtenerClub(@Path("id") int clubId, @Query("activado") boolean activado);

    /**
     * API para obtener los clubs favoritos del cliente
     */
    @GET("api/favorito/")
    Call<Favorito> obtenerFavorito(@Query("cliente__id") int clienteId, @Query("sucursal__id") int sucursalId);

    /**
     * API para obtener un usuario por su correo
     */
    @GET("api/user/")
    Call<User> obtenerUsuario(@Query("email") String email);

    /**
     * API para obtener un usuario por su id
     */
    @GET("api/user/{id}/")
    Call<User> obtenerUsuario(@Path("id") int id);

    /**
     * API para actualizar la contraseña del usuario
     */
    @FormUrlEncoded
    @PATCH("api/user/{id}/")
    Call<User> actualizarContrasena(@Path("id") int id, @Field("password") String password);

    /**
     * API para actualizar el correo del usuario
     */
    @FormUrlEncoded
    @PATCH("api/user/{id}/")
    Call<User> actualizarCorreo(@Path("id") int id, @Field("email") String email);

    /**
     * API para actualizar el nombre, apellido y teléfono del cliente
     */
    @FormUrlEncoded
    @PATCH("api/cliente/{id}/")
    Call<Cliente> actualizarCliente(@Path("id") int id,
                                    @Field("nombre") String nombre,
                                    @Field("apellido") String apellido,
                                    @Field("telefono") String telefono);

//    /**
//     *  API para actualizar la información del cliente
//     */
//    @PATCH("api/user/{id}/")
//    Call<Cliente> actualizarCliente(@Path("id") int id, @Body Cliente cliente);

    /**
     * API para obtener favoritos por cliente
     */
    @GET("api/favorito/")
    Call<Favorito> obtenerFavoritos(@Query("cliente__id") int clienteId);

    /**
     * API para agregar favoritos
     */
    @FormUrlEncoded
    @POST("api/favorito/")
    Call<Favorito> agregarFavorito(@Field("cliente") int cliente, @Field("sucursal") int sucursal);

    /**
     * API para remover favoritos
     */
    @DELETE("api/favorito/{id}/")
    Call<ResponseBody> removerFavorito(@Path("id") int favoritoId);

    /**
     * API para actualizar foto del usuario
     */
    @Multipart
    @POST("actualizarFotoCliente/")
    Call<ResponseBody> actualizarFoto(@Part("clienteID") RequestBody clienteId, @Part MultipartBody.Part file);

    /**
     * API para asignar un id de Conekta a un cliente
     */
    @FormUrlEncoded
    @POST("addCustomerConekta/")
    Call<ResponseBody> crearCustomerConekta(@Field("clienteID") String idCliente);

    /**
     * API para borrar un cliente de Conekta por el cliente id
     */
    @FormUrlEncoded
    @POST("deleteCustomerConekta/")
    Call<ResponseBody> borrarCustomerConekta(@Field("clienteID") String idCliente);

    /**
     * API para añadir un método de pago de Conekta
     */
    @FormUrlEncoded
    @POST("addMetodoPagoConekta/")
    Call<ResponseBody> añadirMetodoPagoConekta(@Field("clienteID") String idCliente, @Field("token") String token);

    /**
     * API para borrar un método de pago de Conekta
     */
    @FormUrlEncoded
    @POST("deleteMetodoPagoConekta/")
    Call<ResponseBody> borrarMetodoPagoConekta(@Field("clienteID") int clienteID, @Field("metodoID") String metodoID);

    /**
     * API para obtener los servicios por sucursal
     */
    @GET("api/servicioClub/")
    Call<ServicioClub> obtenerServicios(@Query("sucursal__id") int idSucursal);

    /**
     * API para obtener un servicio por su id
     */
    @GET("api/servicio/{id}/")
    Call<Servicio> obtenerServicio(@Path("id") int idServicio);

    /**
     * API para obtener métodos de pago de Conekta por cliente id
     */
    @FormUrlEncoded
    @POST("metodosPagoConekta/")
    Call<MetodosPago> obtenerMetodosPagoConekta(@Field("clienteID") String clienteID);

    /**
     * API para obtener las actividades por sucursal id
     */
    @GET("api/actividadClub/")
    Call<ActividadClub> obtenerActividades(@Query("sucursal__id") int idSucursal);

    /**
     * API para obtener una actividad por su id
     */
    @GET("api/actividad/{id}/")
    Call<Actividad> obtenerActividad(@Path("id") int idActividad);

    /**
     * API para obtener el horario de una actividad según su id y el día
     */
    @GET("api/actividadHorario/")
    Call<ActividadHorario> obtenerHorarioActividad(@Query("actividadClub__id") int idActividadClub);

    /**
     * API para verificar que un usuario ya haya hecho la evaluación de una sucursal
     */
    @GET("api/evaluacionSucursal/")
    Call<EvaluacionSucursal> verificarEvaluacionUsuario(@Query("cliente__id") int idCliente, @Query("sucursal__id") int idSucursal);

    /**
     * API para crear una evaluacion de sucursal
     */
    @POST("api/evaluacionSucursal/")
    Call<EvaluacionSucursal> crearEvaluacion(@Body EvaluacionSucursal evaluacionSucursal);

    /**
     * API para calcular el promedio de las evaluaciones de la sucursal
     */
    @FormUrlEncoded
    @POST("api/calcularPromedioEvaluaciones/")
    Call<PromedioEvaluacion> calcularPromedioSucursal(@Field("idSucursal") int idSucursal);

    /**
     * API para actualizar la ciudad y el estado de un cliente
     */
    @FormUrlEncoded
    @PATCH("api/cliente/{id}/")
    Call<Cliente> actualizarCiudadEstadoCliente(@Path("id") int idCliente, @Field("ciudad") int idCiudad, @Field("estado") int idEstado);

    /**
     * API para consultar la fecha del servidor
     */
    @POST("consultarFecha/")
    Call<Fecha> consultarFecha();

    /**
     * API para consultar
     */
    @GET("api/sucursal/")
    Call<Sucursal> obtenerSucursalMayorMenor(@Query("maximo") String maximo, @Query("minimo") String minimo, @Query("activa") boolean activado);

    /**
     * API para comprobar si tiene sesiones en la sucursal encontrada
     */
    @GET("api/sesion/")
    Call<Sesion> comprobarSesionesSucursal(@Query("sucursal__id") String sucursalID, @Query("cliente__id") String clienteID, @Query("activo") boolean activado);

    /**
     * API para descontar una sesión a un cliente
     */
    @PUT("api/sesion/{id}/")
    Call<Sesion> descontarSesion(@Path("id") int id, @Body Sesion sesion);

    /**
     * API para obtener las sesiones totales del cliente de todas las sucursales
     */
    @GET("api/sesion/")
    Call<Sesion> obtenerSesionesCliente(@Query("cliente__id") int clienteID, @Query("activo") boolean activo);

    /**
     * API para obtener las sesiones totales del cliente de una sucursal
     */
    @GET("api/sesion/")
    Call<Sesion> obtenerSesionesClienteSucursal(@Query("cliente__id") int clienteID, @Query("sucursal__id") int sucursalID, @Query("activo") boolean activo);

    /**
     * API para obtener las subscripciones mensuales totales del cliente de todas las sucursales
     */
    @GET("api/subscripcion/")
    Call<SubscripcionMes> obtenerSubscripcionesCliente(@Query("cliente__id") int clienteID, @Query("activa") boolean activo);

    /**
     * API para obtener las subscripciones gratuitas del cliente
     */
    @GET("api/subscripcionFree/")
    Call<SubscripcionesGratis> obtenerSubscripcionesGratis(@Query("cliente__id") int clienteID, @Query("activa") boolean activo);

    /**
     * API para obtener las subscripciones mensuales totales del cliente de todas las sucursales
     */
    @GET("api/subscripcion/")
    Call<SubscripcionMes> obtenerSubscripcionesClienteSucursal(@Query("cliente__id") int clienteID, @Query("sucursal__id") int sucursalID, @Query("activa") boolean activo);

    /**
     * API para añadir la visita de una cliente en una sucursal
     */
    @FormUrlEncoded
    @POST("api/visita/")
    Call<Visita> registrarVisita(@Field("cliente") int clienteID, @Field("sucursal") int sucursalID);


    /**
     * API para comprobar la visita de un cliente
     */
    @GET("api/visita/")
    Call<Visita> comprobarVisita(@Query("cliente__id") int clienteID, @Query("sucursal__id") int sucursalID, @Query("fecha") String fecha);

    /**
     * API para comprobar la visita de un cliente 2.0
     */
    @GET("api/visita/")
    Call<Visita> comprobarVisita(@Query("cliente__id") int clienteID, @Query("sucursal__id") int sucursalID);

    /**
     * API para traer todas las visitas de un cliente en cierta sucursal
     */
    @GET("api/visita/")
    Call<Visita> obtenerVisitasCalendario(@Query("cliente__id") int clienteID);

    /**
     * API para verificar si un cliente no tiene su objetivo seleccionado
     */
    @GET("api/cliente/{id}/")
    Call<Cliente> verificarSeleccionObjetivo(@Path("id") int clienteID, @Query("salud") boolean salud, @Query("convivir") boolean convivir, @Query("vermeBien") boolean vermeBien, @Query("diversion") boolean diversion);

    /**
     *  API para consultar el horario de una sucursal
     */
//    @GET("api/horario/")
//    Call<Horario> consultarHorarioSucursal(@Query("sucursal__id") int sucursalID);

    /**
     * API para registrar la subscripcion mensual de un cliente
     */
    @FormUrlEncoded
    @POST("SubscripcionMensual/")
    Call<ResponseBody> registrarSubscripcion(@Field("clienteID") int clienteID, @Field("sucursalID") int sucursalID, @Field("direccion") String direccion);

    /**
     * API para poder cancelar subscripcion
     */
    @FormUrlEncoded
    @POST("CancelarSubscripcionMensual/")
    Call<ResponseBody> cancelarSubscripcion(@Field("clienteID") int clienteID, @Field("subscripcionID") int subscripcionID);

    /**
     * API para comprar un paquete de cierta sucursal por sesiones
     */
    @FormUrlEncoded
    @POST("CobrarPorSesion/")
    Call<ResponseBody> cobrarPorSesion(@Field("clienteID") int clienteID, @Field("sucursalID") int sucursalID, @Field("metodoPagoID") String metodoPagoID, @Field("numeroSesiones") int numSesiones);

    /**
     * Api para crear una sesion
     */
    @POST("api/sesion/")
    Call<Sesion> crearSesion(@Body Sesion sesion);

    /**
     * API para crear una subscripción
     */
    @POST("api/subscripcion/")
    Call<Suscripcion> crearSubscripcion(@Body SubscripcionPojo suscripcion);

    /**
     * API para obtener la información completa de una sucursal
     */
    @FormUrlEncoded
    @POST("perfilSucursal/")
    Call<PerfilSucursal> obtenerPerfilSucursal(@Field("sucursalID") int sucursalID, @Field("clienteID") int clienteID);

    /**
     * API para obtener los dias disponibles de la sucursal
     */
    @FormUrlEncoded
    @POST("diasDisponibles/")
    Call<ResponseBody> obtenerDiasDisponibles(@Field("sucursalID") int sucursalID);

    /**
     * API para remover sesiones
     */
    @DELETE("api/sesion/{id}/")
    Call<ResponseBody> removerSesion(@Path("id") int sesionID);

    /**
     * API para remover subscripciones
     */
    @DELETE("api/subscripcion/{id}/")
    Call<ResponseBody> removerSubscripcion(@Path("id") int subscripcionID);

    /**
     * API para actualizar el id del cliente
     */
    @FormUrlEncoded
    @PATCH("api/cliente/{id}/")
    Call<Cliente> actualizarPlayerID(@Path("id") int clienteID, @Field("playerID") String playerID);

    /**
     * API para actualizar el token id de facebook
     */
    @FormUrlEncoded
    @PATCH("api/cliente/{id}/")
    Call<Cliente> actualizarFacebookToken(@Path("id") int clienteID, @Field("idFacebook") String idFacebook);

    /**
     * API para actualizar el token id de google
     */
    @FormUrlEncoded
    @PATCH("api/cliente/{id}/")
    Call<Cliente> actualizarGoogleToken(@Path("id") int clienteID, @Field("idGoogle") String idGoogle);

    /**
     * API para revisar la asistencia de un cliente
     */
    @FormUrlEncoded
    @POST("RevisarVisita/")
    Call<Asistencia> revisarAsistencia(@Field("idCliente") int idCliente, @Field("beacon") String beacon, @Field("maximo") int maximo, @Field("minimo") int minimo);

    /**
     * API para revisar la asistencia de un cliente
     */
    @FormUrlEncoded
    @POST("RegistrarVisita/")
    Call<RegistroAsistencia> registrarAsistencia(@Field("idCliente") int idCliente, @Field("idSucursal") int idSucursal);

    /**
     * API para desactivar sesiones
     */
    @FormUrlEncoded
    @PATCH("api/sesion/{id}/")
    Call<Sesion> desactivarSesion(@Path("id") int sesionID, @Field("activo") boolean activo);

    /**
     * API para obtener las asistencias de ciertas sucursal
     */
    @GET("api/visita/")
    Call<Visita> obtenerVisitas(@Query("sucursal__id") int sucursalID, @Query("cliente__id") int clienteID);

    /**
     * API para revisar la promoción gratuita del cliente
     */
    @FormUrlEncoded
    @POST("RevisarSubscripcionFree/")
    Call<RevisionSubscripcionFree> revisarSubscripcionFree(@Field("idCliente") int idCliente,@Field("idSucursal") int idSucursal);

    /**
     * API para activar la promoción gratuita del cliente
     */
    @FormUrlEncoded
    @POST("ActivarSubscripcionFree/")
    Call<ActivarSubscripcionFree> activarSubscripcionFree(@Field("idCliente") int idCliente, @Field("idSucursal") int idSucursal);

    /**
     * API para registrar un teléfono que no se repita en la agenda
     */
    @FormUrlEncoded
    @POST("RegistrarCelular/")
    Call<RegistrarCelularResponse> registrarCelular(@Field("correo") String correo,
                                                    @Field("password") String contrasena,
                                                    @Field("nombre") String nombre,
                                                    @Field("apellido") String apellido,
                                                    @Field("hombre") boolean hombre,
                                                    @Field("mujer") boolean mujer,
                                                    @Field("celular") String telefono,
                                                    @Field("social_media") String social_media,
                                                    @Field("social_media_code") String social_media_code);

    /**
     *  API para revisar la verificación de un teléfono celular
     */
    @FormUrlEncoded
    @POST("CheckPhone/")
    Call<CheckPhoneResponse> checkPhone(@Field("celular") String celular);

    /**
     *  API para revisar la verificación de un teléfono celular
     */
    @FormUrlEncoded
    @POST("UpdatePhone/")
    Call<UpdatePhoneResponse> updatePhone(@Field("celular") String celular,@Field("idCliente") int idCliente);

    /**
     *  API para verificar el código enviado al celular
     */
    @FormUrlEncoded
    @POST("VerifyPhone/")
    Call<VerifyPhoneResponse> verifyPhone(@Field("celular") String celular, @Field("sms_code") String sms_code);

    /**
     *  API para obtener subscripciones del cliente
     */
    @FormUrlEncoded
    @POST("GetSubscriptions/")
    Call<GetSubscripcionesResponse> obtenerSubscripciones(@Field("idCliente") int idCliente);

}

