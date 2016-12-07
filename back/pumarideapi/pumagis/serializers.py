from rest_framework import serializers
from .models import Point
from .models import Line

class PointSerializer(serializers.ModelSerializer):
    class Meta:
        model = Point

class LineSerializer(serializers.ModelSerializer):
    class Meta:
        model = Line
        fields= ('id','user_id','p_origen','p_destino','shortest_path','origin_point','destination_point','tipo_transporte',)
