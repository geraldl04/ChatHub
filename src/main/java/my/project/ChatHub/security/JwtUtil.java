package my.project.ChatHub.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

//deklaruar si component qe sherben si baze per service etc  , springu i deteketon automatikisht klasat e anotuara me component
//dhe krijon instanca per to
@Component
public class JwtUtil {

    //gjeneron nje random sekrt key ne baze te alg h2512 , jo suitable per productions epse ndryshon cdo here qe behet restart appi
    //te perdor nje constant string key , por po e le keshtu
    // private final SecretKey jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    //jwt eshte nje string qe represents nje set of claims si nje object Json , ku claim eshte nje cope informacioni  qe i asenjohet nje subjekti
    //nje claim eshte i llojit te ciftit name/value

    //krijon nje token me subject emailin e userit pra te identifikohet qe ky token i perket keti useri , ndersa signWith i thote
    //"paketoje kete token me nje paketim sekret , qe askysh te mos mund ta "beje fallco"
    public String generateToken(String emaili) {
        return Jwts.builder()
                .setSubject(emaili)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                //i shenon jwt token qe do gjeneorhet kete secret key ne baz te alg , ne menyre qe te shtoj security ,
                //pra nese ne ndonje menyre kapet tokeni , shenjuesi eshte shume me e veshtire te kapet
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                //lidhi te gjitha ne nje string
                .compact();
    }

    //opsioni i pare

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()

                //po te jap celesin qe mund ti beje unlock keti tokeni
                .setSigningKey(jwtSecretKey)
                .build()

                //hep tokenin dhe lexo claims brenda tij
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public boolean validateJwtToken(String token) {
        try {
            //provo te lexosh tokenin duker perdorur secret key nese jep nje exception shfaqe
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }
    //opsioni 2
//    public String extractUserName(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(jwtSecretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUserName(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }

    //menyra tjeter me secret key , e deklaroj klasen si service , menyre tjt deklaroj nje string secret key shume te gjate
    //qe tmos me jap runtime exception dhe e perdor te signwith kur tjem duke krijuarar tokenin
//    private String secretkey = "";
//
//    public JwtUtil() {
//
//        try {
//            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//            SecretKey sk = keyGen.generateKey();
//            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public String generateToken(String username) {
//        Map<String, Object> claims = new HashMap<>();
//        return Jwts.builder()
//                .addClaims(claims)
//                .setSubject(username)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))
//                .signWith(getKey())
//                .compact();
//
//    }
//
//    private Key getKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
}