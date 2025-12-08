from rest_framework import serializers
from .models import PatientMedicalData

class PatientMedicalDataSerializer(serializers.ModelSerializer):
    nombre_paciente = serializers.ReadOnlyField(source='patient.patient_first_name')
    apellido_paciente = serializers.ReadOnlyField(source='patient.patient_last_name')

    class Meta:
        model = PatientMedicalData
        fields = '__all__' # Al poner all, automáticamente incluye los dos nuevos de arriba
        read_only_fields = (
            'created_by', 
            'modified_by', 
            'created_at', 
            'updated_at',
            'active'
        )