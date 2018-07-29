## Beacon_Final
>@ I-Beacon 을 사용한 실내위치 데이터를 서버로 보내는 프로그램이다.<br>
비콘과의 연결을 주기적으로 Check 한다음, 가장 가까이 있는 비콘의 id를 통해 사용자의 실내 위치를 알수있다<br>
여러사용자의 위치 데이터가 중복되지 않도록 Android Device ID 를 서버에 같이 전송한다.<br>
Device ID가 노출되면 부적절한 상황을 야기할 수 있기 때문에 이를 방지하기 위해 Noise 를 추가하는 함수도 존재한다. ( noBid, sencondnoise)<br>
Estimote Beacon을 사용했다.
